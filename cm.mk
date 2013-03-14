## Specify phone tech before including full_phone
$(call inherit-product, vendor/cm/config/gsm.mk)

# Inherit some common CM stuff.
$(call inherit-product, vendor/cm/config/common_full_phone.mk)

# This device has NFC
$(call inherit-product, vendor/cm/config/nfc_enhanced.mk)

# Inherit device configuration
$(call inherit-product, device/lge/p760/p760.mk)

PRODUCT_NAME := cm_p760

# Release name and versioning
PRODUCT_RELEASE_NAME := OptimusL9
PRODUCT_VERSION_DEVICE_SPECIFIC :=
-include vendor/cm/config/common_versions.mk

PRODUCT_BUILD_PROP_OVERRIDES += PRODUCT_NAME=u2_open_eu BUILD_FINGERPRINT=lge/u2_open_eu/u2:4.1.2/JRO03L/P76020b.4DAAAC8F:user/release-keys PRIVATE_BUILD_DESC="u2_open_eu-user 4.1.2 JRO03L P76020b.4DAAAC8F release-keys"

## Device identifier. This must come after all inclusions
PRODUCT_DEVICE := p760

# Enable Torch
PRODUCT_PACKAGES += Torch WiFiDirectDemo

