$(call inherit-product, $(SRC_TARGET_DIR)/product/languages_full.mk)

$(call inherit-product-if-exists, vendor/lge/p760/p760-vendor.mk)

$(call inherit-product, device/common/gps/gps_eu.mk)

DEVICE_PACKAGE_OVERLAYS += device/lge/p760/overlay

$(call inherit-product, frameworks/native/build/phone-xhdpi-1024-dalvik-heap.mk)

PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/recovery/postrecoveryboot.sh:recovery/root/sbin/postrecoveryboot.sh

## Scripts and confs
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/init.p760.usb.rc:root/init.p760.usb.rc \
    $(LOCAL_PATH)/init.lgep760board.rc:root/init.lgep760board.rc \
    $(LOCAL_PATH)/ueventd.lgep760board.rc:root/ueventd.lgep760board.rc \
    $(LOCAL_PATH)/fstab.u2:root/fstab.lgep760board

PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/wpa_supplicant.conf:system/etc/wifi/wpa_supplicant.conf \
    $(LOCAL_PATH)/configs/hostapd.conf:system/etc/wifi/softap/hostapd.conf \
    $(LOCAL_PATH)/configs/touch_dev.idc:system/usr/idc/touch_dev.idc \
    $(LOCAL_PATH)/configs/touch_dev.kl:system/usr/keylayout/touch_dev.kl

# stagefright confs
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/media_profiles.xml:system/etc/media_profiles.xml \
    $(LOCAL_PATH)/configs/media_codecs.xml:system/etc/media_codecs.xml \
    $(LOCAL_PATH)/audio/audio_policy.conf:system/etc/audio_policy.conf

# wifi nvram calibration
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/bcmdhd.cal:system/etc/wifi/bcmdhd.cal

# RIL stuffs
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/ipc_channels.config:system/etc/ipc_channels.config

# NFC stuffs
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/nfcee_access.xml:system/etc/nfcee_access.xml

PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/bt_vendor.conf:system/etc/bluetooth/bt_vendor.conf

# Permission files
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/handheld_core_hardware.xml:system/etc/permissions/handheld_core_hardware.xml \
    frameworks/native/data/etc/android.hardware.camera.flash-autofocus.xml:system/etc/permissions/android.hardware.camera.flash-autofocus.xml \
    frameworks/native/data/etc/android.hardware.camera.front.xml:system/etc/permissions/android.hardware.camera.front.xml \
    frameworks/native/data/etc/android.hardware.telephony.gsm.xml:system/etc/permissions/android.hardware.telephony.gsm.xml \
    frameworks/native/data/etc/android.hardware.location.gps.xml:system/etc/permissions/android.hardware.location.gps.xml \
    frameworks/native/data/etc/android.hardware.wifi.xml:system/etc/permissions/android.hardware.wifi.xml \
    frameworks/native/data/etc/android.hardware.sensor.proximity.xml:system/etc/permissions/android.hardware.sensor.proximity.xml \
    frameworks/native/data/etc/android.hardware.sensor.compass.xml:system/etc/permissions/android.hardware.sensor.compass.xml \
    frameworks/native/data/etc/android.software.sip.voip.xml:system/etc/permissions/android.software.sip.voip.xml \
    frameworks/native/data/etc/android.hardware.touchscreen.multitouch.jazzhand.xml:system/etc/permissions/android.hardware.touchscreen.multitouch.jazzhand.xml \
    frameworks/native/data/etc/android.hardware.nfc.xml:system/etc/permissions/android.hardware.nfc.xml \
    frameworks/native/data/etc/android.hardware.usb.accessory.xml:system/etc/permissions/android.hardware.usb.accessory.xml

## GPS
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/gps_brcm_conf.xml:system/etc/gps_brcm_conf.xml

$(call inherit-product, build/target/product/full.mk)

PRODUCT_AAPT_CONFIG := normal hdpi

PRODUCT_PACKAGES += \
    wifimac

PRODUCT_PACKAGES += \
    lights.omap4 \
    libaudioutils \
    audio.a2dp.default \
    audio_policy.default \
    audio.primary.p760 \
    hwcomposer.p760

PRODUCT_PACKAGES += \
    OMXCore \
    libOMX_Core \
    libdomx \
    libOMX.TI.DUCATI1.VIDEO.H264E \
    libOMX.TI.DUCATI1.VIDEO.MPEG4E \
    libOMX.TI.DUCATI1.VIDEO.CAMERA \
    libOMX.TI.DUCATI1.MISC.SAMPLE \
    libOMX.TI.DUCATI1.VIDEO.DECODER.secure \
    libOMX.TI.DUCATI1.VIDEO.DECODER
#
PRODUCT_PACKAGES += \
    libtiutils \
    libipcutils \
    libipc \
    libnotify \
    syslink_trace_daemon.out \
    librcm \
    libsysmgr \
    syslink_daemon.out \
    dmm_daemontest.out \
    event_listener.out \
    interm3.out \
    gateMPApp.out \
    heapBufMPApp.out \
    heapMemMPApp.out \
    listMPApp.out \
    messageQApp.out \
    nameServerApp.out \
    sharedRegionApp.out \
    memmgrserver.out \
    notifyping.out \
    ducati_load.out \
    procMgrApp.out \
    slpmresources.out \
    slpmtransport.out \
    utilsApp.out \
    libd2cmap \
    libomap_mm_library_jni \
    libtimemmgr

# NFC packages
PRODUCT_PACKAGES += \
    libnfc \
    libnfc_jni \
    Nfc \
    Tag

FRAMEWORKS_BASE_SUBDIRS += \
	$(addsuffix /java, omapmmlib )

#FRAMEWORKS_BASE_SUBDIRS += \
#	../../$(LOCAL_PATH)/framework-addons/

PRODUCT_PACKAGES += \
    libskiahwdec \
    libskiahwenc

PRODUCT_PACKAGES += \
    libstagefrighthw

#copy firmware
PRODUCT_COPY_FILES += \
  system/bluetooth/data/main.conf:system/etc/bluetooth/main.conf

# Charger mode
PRODUCT_PACKAGES += \
    charger \
    charger_res_images

PRODUCT_BUILD_PROP_OVERRIDES += BUILD_UTC_DATE=0
PRODUCT_NAME := full_p760
PRODUCT_DEVICE := p760
PRODUCT_MODEL := LG-P760
PRODUCT_MANUFACTURER := LGE
