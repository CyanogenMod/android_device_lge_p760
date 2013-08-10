/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.internal.telephony;

import static com.android.internal.telephony.RILConstants.*;

import android.content.Context;

import android.media.AudioManager;

import android.os.AsyncResult;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;

import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import android.util.Log;

public class U2RIL extends RIL implements CommandsInterface {

    private AudioManager audioManager;
    protected HandlerThread mPathThread;
    protected CallPathHandler mPathHandler;

    private int mCallPath = -1;

    public U2RIL(Context context, int networkMode, int cdmaSubscription) {
        super(context, networkMode, cdmaSubscription);
        PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                /* Higher state wins, unless going back to idle */
                if (state == TelephonyManager.CALL_STATE_IDLE || state > mCallState)
                    mCallState = state;


                /* Loop a speakerphone status check while offhook, to
                   adjust the model call path accordingly */
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    if (mPathHandler != null) {
                        mPathHandler.checkSpeakerphoneState();
                    }
                }
            }
        };

        // register for phone state notifications.
        ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE))
            .listen(mPhoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE);

        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        if (mPathHandler == null) {
            mPathThread = new HandlerThread("CallPathThread");
            mPathThread.start();
            mPathHandler = new CallPathHandler(mPathThread.getLooper());
            mPathHandler.run();
        }
    }

    protected int mCallState = TelephonyManager.CALL_STATE_IDLE;

    private int RIL_REQUEST_HANG_UP_CALL = 0xb7;
    private int RIL_REQUEST_LGE_CPATH = 0xfd;

    /* We're not actually changing REQUEST_GET_IMEI, but it's one
       of the first requests made after enabling the radio, and it
       isn't repeated while the radio is on, so a good candidate to
       inject initialization ops */

    @Override
    public void
    getIMEI(Message result) {
        //RIL_REQUEST_LGE_SEND_COMMAND
        // Use this to bootstrap a bunch of internal variables
        RILRequest rrLSC = RILRequest.obtain(
                0x113, null);
        rrLSC.mp.writeInt(1);
        rrLSC.mp.writeInt(0);
        send(rrLSC);


        // The original (and unmodified) IMEI request
        RILRequest rr = RILRequest.obtain(RIL_REQUEST_GET_IMEI, result);

        if (RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));

        send(rr);
    }

    @Override
    public void
    hangupWaitingOrBackground (Message result) {
        RILRequest rr = RILRequest.obtain(mCallState == TelephonyManager.CALL_STATE_OFFHOOK ?
                                        RIL_REQUEST_HANGUP_WAITING_OR_BACKGROUND :
                                        RIL_REQUEST_HANG_UP_CALL,
                                        result);

        if (RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));

        send(rr);
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    static final int RIL_UNSOL_LGE_BATTERY_LEVEL_UPDATE = 1050;
    static final int RIL_UNSOL_LGE_XCALLSTAT = 1053;
    static final int RIL_UNSOL_LGE_SIM_STATE_CHANGED = 1060;
    static final int RIL_UNSOL_LGE_SIM_STATE_CHANGED_NEW = 1061;
    static final int RIL_UNSOL_LGE_SELECTED_SPEECH_CODEC = 1074;

    @Override
    protected void
    processUnsolicited (Parcel p) {
        Object ret;
        int dataPosition = p.dataPosition(); // save off position within the Parcel
        int response = p.readInt();

        switch(response) {
            case RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED: ret =  responseVoid(p); break;
            case RIL_UNSOL_ON_USSD: ret =  responseStrings(p); break;
            case RIL_UNSOL_LGE_XCALLSTAT: ret =  responseInts(p); break;
            case RIL_UNSOL_LGE_SELECTED_SPEECH_CODEC: ret =  responseVoid(p); break;
            case RIL_UNSOL_LGE_BATTERY_LEVEL_UPDATE: ret =  responseVoid(p); break;
            case RIL_UNSOL_LGE_SIM_STATE_CHANGED:
            case RIL_UNSOL_LGE_SIM_STATE_CHANGED_NEW: ret =  responseVoid(p); break;

            default:
                // Rewind the Parcel
                p.setDataPosition(dataPosition);

                // Forward responses that we are not overriding to the super class
                super.processUnsolicited(p);
                return;
        }
        switch(response) {
            case RIL_UNSOL_LGE_XCALLSTAT:
                int[] intArray = (int[]) ret;
                int xcallState = intArray[1];
                /* 0 - established
                 * 2 - dial start
                 * 3 - dialing
                 * 4 - incoming
                 * 6 - hangup
                 * 7 - answered
                 */
                mCallPath = -1;
                switch (xcallState) {
                    case 0:
                        mCallPath = 1;
                        break;
                    /*case 3:
                    case 4:*/
                    case 6:
                        mCallPath = 0;
                        break;
                    default:
                        break;
                }
                if (RILJ_LOGD) riljLog("LGE call state change > " + intArray[1]);
                if (mCallPath >= 0) {
                    RILRequest rrLSL = RILRequest.obtain(
                            RIL_REQUEST_LGE_CPATH, null);
                    rrLSL.mp.writeInt(1);
                    rrLSL.mp.writeInt(mCallPath);
                    send(rrLSL);
                }

                break;
            case RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED:
                /* has bonus radio state int */
                RadioState newState = getRadioStateFromInt(p.readInt());
                p.setDataPosition(dataPosition);
                super.processUnsolicited(p);
                if (RadioState.RADIO_ON == newState) {
                    setNetworkSelectionModeAutomatic(null);
                }
                return;
            case RIL_UNSOL_ON_USSD:
                String[] resp = (String[])ret;

                if (resp.length < 2) {
                    resp = new String[2];
                    resp[0] = ((String[])ret)[0];
                    resp[1] = null;
                }
                if (resp[1].length()%2 == 0 && resp[1].matches("[0-9A-F]+")) {
                    try { 
                        resp[1] = new String(hexStringToByteArray(resp[1]), "UTF-16");
                    } catch (java.io.UnsupportedEncodingException uex) { 
                        // encoding not supported, should never get here 
                    } catch (java.io.IOException iox) { 
                        // you will get here if the original sequence wasn't UTF-8 or ASCII 
                    } 
                }
                if (RILJ_LOGD) unsljLogMore(response, resp[0]);
                if (mUSSDRegistrant != null) {
                    mUSSDRegistrant.notifyRegistrant(
                        new AsyncResult (null, resp, null));
                }
                break;
            case 1080: // RIL_UNSOL_LGE_FACTORY_READY (NG)
                /* Adjust request IDs */
                RIL_REQUEST_HANG_UP_CALL = 0xb7;
                break;
            case RIL_UNSOL_LGE_SIM_STATE_CHANGED:
            case RIL_UNSOL_LGE_SIM_STATE_CHANGED_NEW:
                if (RILJ_LOGD) unsljLog(response);

                if (mIccStatusChangedRegistrants != null) {
                    mIccStatusChangedRegistrants.notifyRegistrants();
                }
                break;
            case RIL_UNSOL_LGE_BATTERY_LEVEL_UPDATE:
            case RIL_UNSOL_LGE_SELECTED_SPEECH_CODEC:
                if (RILJ_LOGD) riljLog("sinking LGE request > " + response);
        }

    }

    class CallPathHandler extends Handler implements Runnable {

        public CallPathHandler (Looper looper) {
            super(looper);
        }

        private void checkSpeakerphoneState() {
            if (mCallState == TelephonyManager.CALL_STATE_OFFHOOK) {
                int callPath = -1;
                if (audioManager.isSpeakerphoneOn()) {
                    callPath = 3;
                } else {
                    callPath = 1;
                }

                if (callPath != mCallPath) {
                    mCallPath = callPath;
                    RILRequest rrLSL = RILRequest.obtain(
                            RIL_REQUEST_LGE_CPATH, null);
                    rrLSL.mp.writeInt(1);
                    rrLSL.mp.writeInt(callPath);
                    send(rrLSL);
                }

                Message msg = obtainMessage();
                msg.what = 0xc0ffee;
                sendMessageDelayed(msg, 2500);
            }
        }

        @Override
        public void handleMessage (Message msg) {
            if (msg.what == 0xc0ffee) {
                if (mCallState == TelephonyManager.CALL_STATE_OFFHOOK) {
                    checkSpeakerphoneState();
                }
            }
        }

        @Override
        public void run () {
        }
    }

}
