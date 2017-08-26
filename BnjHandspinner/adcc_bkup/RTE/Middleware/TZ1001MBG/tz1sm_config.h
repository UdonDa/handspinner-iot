/**
 * @file tz1sm_config.h
 * @brief a header file for TZ10xx TWiC for Bluetooth 4.0 Smart
 * @version V2.0.0
 * @note
 */

/*
 * COPYRIGHT (C) 2014
 * TOSHIBA CORPORATION SEMICONDUCTOR & STORAGE PRODUCTS COMPANY
 * ALL RIGHTS RESERVED.
 *
 * THE SOURCE CODE AND ITS RELATED DOCUMENTATION IS PROVIDED "AS
 * IS". TOSHIBA CORPORATION MAKES NO OTHER WARRANTY OF ANY KIND,
 * WHETHER EXPRESS, IMPLIED OR, STATUTORY AND DISCLAIMS ANY AND ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY, SATISFACTORY QUALITY, NON
 * INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * THE SOURCE CODE AND DOCUMENTATION MAY INCLUDE ERRORS. TOSHIBA
 * CORPORATION RESERVES THE RIGHT TO INCORPORATE MODIFICATIONS TO THE
 * SOURCE CODE IN LATER REVISIONS OF IT, AND TO MAKE IMPROVEMENTS OR
 * CHANGES IN THE DOCUMENTATION OR THE PRODUCTS OR TECHNOLOGIES
 * DESCRIBED THEREIN AT ANY TIME.
 * 
 * TOSHIBA CORPORATION SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT OR
 * CONSEQUENTIAL DAMAGE OR LIABILITY ARISING FROM YOUR USE OF THE
 * SOURCE CODE OR ANY DOCUMENTATION, INCLUDING BUT NOT LIMITED TO,
 * LOST REVENUES, DATA OR PROFITS, DAMAGES OF ANY SPECIAL, INCIDENTAL
 * OR CONSEQUENTIAL NATURE, PUNITIVE DAMAGES, LOSS OF PROPERTY OR LOSS
 * OF PROFITS ARISING OUT OF OR IN CONNECTION WITH THIS AGREEMENT, OR
 * BEING UNUSABLE, EVEN IF ADVISED OF THE POSSIBILITY OR PROBABILITY
 * OF SUCH DAMAGES AND WHETHER A CLAIM FOR SUCH DAMAGE IS BASED UPON
 * WARRANTY, CONTRACT, TORT, NEGLIGENCE OR OTHERWISE.
 *
 */

#ifndef _TZ1SM_CONFIG_H_
#define _TZ1SM_CONFIG_H_


/*
 * Please check RTE_Device.h for GPIO 28,29,30 and 31.
 * These GPIO should be enabled for Bluetooth features.
 * UA2 HW FLOW must be enabled "RTE_UART2_HW_FLOW 1".
 *
 */


/*
 * Board choice
 *
 */
#undef TZ1XXX_BOARD_DEVELOPER_A
#undef TZ1XXX_BOARD_REFERENCE_A
#undef TZ1XXX_BOARD_REFERENCE_B
#define TZ1XXX_BOARD_REFERENCE_X
#undef TZ1XXX_BOARD_EXTERIOR_A


/*
 * NON-OS, RTOS
 *
 */
#define TWIC_RTOS__NONOS
#undef TWIC_RTOS__CMSIS_RTOS
#undef TWIC_RTOS__FREERTOS
#undef TWIC_RTOS__FREERTOS_TICKLESS


/*
 * Define TWIC_SCAN if the LE_Scan_Enable is used otherwise
 * disable it for saving memory (-2.5kB).
 *
 */
#define TWIC_CONFIG_ENABLE_SCAN

/*
 * TWIC_CONFIG_RX_BUFFER_SIZE
 * If neither ATT Write Command or GATT Client Notification is used,
 * the size can be 3.
 */
#define TWIC_CONFIG_RX_BUFFER_SIZE 6

/*
 * Define TWIC_SM_INITIATOR if the SMP Initiator feature is used otherwise
 * disable it for saving memory.
 *
 * Define TWIC_SM_RESPONDER if the SMP Responder feature is used otherwise
 * disable it for saving memory.
 *
 */
#define TWIC_CONFIG_SM_INITIATOR
#define TWIC_CONFIG_SM_RESPONDER


/*
 * Define TWIC_API_LE if each API is used otherwise disable it
 * for saving memory.
 *
 */
#define TWIC_API_LELMRESOLVEBDADDR
#define TWIC_API_LEDELWHITELIST
#define TWIC_API_LEADDWHITELIST
#define TWIC_API_LEREADWHITELISTSIZE
#define TWIC_API_LEREADLOCALSUPPORTEDFEATURES
#define TWIC_API_LESETHOSTCHANNELCLASSIFICATION
#define TWIC_API_LEREADCHANNELMAP
#define TWIC_API_LEREADSUPPORTEDSTATES
#define TWIC_API_LELMGENRESOLVABLEBDADDR
#define TWIC_API_LESETRANDOMADDRESS
#define TWIC_API_LESMSETIRVALUE


/*
 * Define TWIC_API_GATTSERVER if each API is used otherwise disable it
 * for saving memory.
 *
 */
#define TWIC_API_GATTSERVERLONGCHARVALREADOUTRESPONSE
#define TWIC_API_GATTSERVERLONGCHARDESPREADOUTRESPONSE
#define TWIC_API_GATTSERVERLONGCHARVALPREPAREWRITEINRESPONSE
#define TWIC_API_GATTSERVERLONGCHARDESPPREPAREWRITEINRESPONSE
#define TWIC_API_GATTSERVEREXECCHARVALWRITEINRESPONSE
#define TWIC_API_GATTSERVEREXECCHARDESPWRITEINRESPONSE


/*
 * Define TWIC_API_GATTCLIENT if each API is used otherwise disable it
 * for saving memory.
 *
 */
#define TWIC_API_GATTCLIENTEXGMTU
#define TWIC_API_GATTCLIENTDISCOVERPRIMARYSERVICE
#define TWIC_API_GATTCLIENTDISCOVERPRIMARYSERVICEBYSERVICEUUID
#define TWIC_API_GATTCLIENTFINDINCLUDEDSERVICE
#define TWIC_API_GATTCLIENTDISCOVERALLCHARACTERISTICS
#define TWIC_API_GATTCLIENTDISCOVERCHARACTERISTICSBYUUID
#define TWIC_API_GATTCLIENTDISCOVERALLDESCRIPTORS
#define TWIC_API_GATTCLIENTREADCHARACTERISTICVALUE
#define TWIC_API_GATTCLIENTREADUSINGCHARACTERISTICUUID
#define TWIC_API_GATTCLIENTWRITECHARACTERISTICVALUE
#define TWIC_API_GATTCLIENTRELIABLEWRITE
#define TWIC_API_GATTCLIENTWRITEWITHOUTRESPONSE
#define TWIC_API_GATTCLIENTSIGNEDWRITEWITHOUTRESPONSE
#define TWIC_API_GATTCLIENTINDICATIONCONFIRMATIONRESPONSE
#define TWIC_API_GATTCLIENTREADCHARACTERISTICDESCRIPTOR
#define TWIC_API_GATTCLIENTREADMULTIPLECHARVALUES
#define TWIC_API_GATTCLIENTWRITECHARACTERISTICDESCRIPTOR
#define TWIC_API_GATTCLIENTREADLONGCHARACTERISTICVALUE
#define TWIC_API_GATTCLIENTREADLONGCHARACTERISTICDESCRIPTOR
#define TWIC_API_GATTCLIENTWRITELONGCHARACTERISTICVALUE
#define TWIC_API_GATTCLIENTWRITELONGCHARACTERISTICDESCRIPTOR


/*
 * Define TWIC_API_LECE if each API is used otherwise disable it for
 * saving memory.
 *
 */
#define TWIC_API_LECEDBUSWRITE
#define TWIC_API_LECEDBUSREAD
#define TWIC_API_LECEMEMORYWRITE
#define TWIC_API_LECEMEMORYREAD


/*
 * GATT Based service utility definition.
 * Enable each Service if the Service Generator is used.
 */
/* GA Service Descripter and Characteristic attribute data. */
#define TWIC_UTIL_GA_SERVICE
/* DI Service Descripter and Characteristic attribute data. */
#define TWIC_UTIL_DI_SERVICE
/* HR Service Descripter and Characteristic attribute data. */
#undef TWIC_UTIL_HR_SERVICE
/* Blood Pressure Service Descripter and Characteristic attribute data. */
#undef TWIC_UTIL_BP_SERVICE
/* User Data Service Descripter and Characteristic attribute data. */
#undef TWIC_UTIL_UD_SERVICE
/* CT Service Descripter and Characteristic attribute data. */
#undef TWIC_UTIL_CT_SERVICE
/* Next DST Change Service */
#undef TWIC_UTIL_NC_SERVICE
/* Reference Time Update Service */
#undef TWIC_UTIL_RU_SERVICE
/* Immediate Alert Service */
#undef TWIC_UTIL_IA_SERVICE
/* Health Thermometer Service */
#undef TWIC_UTIL_HT_SERVICE
/* User defined Service 128bit UUID (Experimental Temporary 128bit Service) */
#define TWIC_UTIL_UU_SERVICE
#define TWIC_UTIL_UU_CHARACTERISTICS
/* ANCS */
#define TWIC_UTIL_ANCS


/*
 * SMP utility definition.
 * Enable each TWIC_UTIL_LESMP if the Service Generator is used.
 * For example, TWIC_UTIL_UD_SERVICE needs SMP.
 *
 */
#undef TWIC_UTIL_LESMP
/* The io capability of PairingRequest */
#define TWIC_UTIL_LESMP_MAS_IO_CAP (TWIC_SMP_IO_CAPABILITY_DISPLAY_YES_NO)
#define TWIC_UTIL_MAS_MAX_ENC_KEY_SIZE (0x10)
/* The io capability of PairingConfirm (TWiC combines the Pairing
 * Response with the Pairing Confirm) */
#define TWIC_UTIL_LESMP_SLV_IO_CAP (TWIC_SMP_IO_CAPABILITY_DISPLAY_ONLY)
                                /* (TWIC_SMP_IO_CAPABILITY_NOINPUT_NO_OUTPUT) */
#define TWIC_UTIL_SLV_MAX_ENC_KEY_SIZE (0x10)


/*
 * TIMER
 *
 */
#define TZ1SM_WAIT TZ1SM_HAL_MSEC_PER_SEC


/*
 * Maximum number for user applications using TWiC.
 * Increase the interfaces in use of BT4.1 ROM.
 *
 * If GAP Central and Peripheral need to be managed as a different
 * piconet, the required number of interface is 2 though the ROM
 * version is 5.
 */
#define TWIC_INTERFACE_MAX (2) /* 1 < TWIC_INTERFACE_MAX < 128 */


/*
 * BUTTON definition. Undef it if you do not use TWiC Button feature.
 *
 * Evaluation Board.
 *  TWIC_BUTTON_GPIO_NO (0)
 *
 * Reference Board.
 *  TWIC_BUTTON_GPIO_NO (3)
 *
 * Please check RTE_Device.h for the GPIO.
 *
 */
#if defined(TZ1XXX_BOARD_DEVELOPER_A)

#define TWIC_BUTTON_GPIO_NO (0)

#elif defined(TZ1XXX_BOARD_REFERENCE_A) || defined(TZ1XXX_BOARD_REFERENCE_B)

#define TWIC_BUTTON_GPIO_NO (3)

#elif defined(TZ1XXX_BOARD_REFERENCE_X) || defined(TZ1XXX_BOARD_EXTERIOR_A)

#define TWIC_BUTTON_GPIO_NO (2)
#define TWIC_BUTTON_GPIO_NO_BB (6)

#endif


/*
 * LED definition. Undef it if you do not use TWiC Led feature.
 *
 * Evaluation Board. (RBTZ1001-1MA)
 *  TWIC_LED_GPIO_LED1 : (no use)
 *  TWIC_LED_GPIO_LED2 : (12)
 *  TWIC_LED_GPIO_LED3 : (13)
 *  TWIC_LED_GPIO_LED4 : (no use)
 *  TWIC_LED_GPIO_LED5 : (no use)
 *
 * Reference Board B. (RBTZ1001-2MA)
 *  TWIC_LED_GPIO_LED1 : (no use)
 *  TWIC_LED_GPIO_LED2 : (8)
 *  TWIC_LED_GPIO_LED3 : (9)
 *  TWIC_LED_GPIO_LED4 : (no use)
 *  TWIC_LED_GPIO_LED5 : (no use)
 *
 * Reference Board X. (RBTZ1001-4MA)
 *  TWIC_LED_GPIO_LED1 : (no use)
 *  TWIC_LED_GPIO_LED2 : (10)
 *  TWIC_LED_GPIO_LED3 : (11)
 *  TWIC_LED_GPIO_LED4 : (no use)
 *  TWIC_LED_GPIO_LED5 : (no use)
 *
 */
#if defined(TZ1XXX_BOARD_DEVELOPER_A)

#undef TWIC_LED_GPIO_LED1
#define TWIC_LED_GPIO_LED2 (12)
#define TWIC_LED_GPIO_LED3 (13)
#undef TWIC_LED_GPIO_LED4
#undef TWIC_LED_GPIO_LED5

#elif defined(TZ1XXX_BOARD_REFERENCE_A) || defined(TZ1XXX_BOARD_REFERENCE_B)

#undef TWIC_LED_GPIO_LED1
#define TWIC_LED_GPIO_LED2 (8)
#define TWIC_LED_GPIO_LED3 (9)
#undef TWIC_LED_GPIO_LED4
#undef TWIC_LED_GPIO_LED5

#elif defined(TZ1XXX_BOARD_REFERENCE_X) || defined(TZ1XXX_BOARD_EXTERIOR_A)

#undef TWIC_LED_GPIO_LED1
#define TWIC_LED_GPIO_LED2 (10)
#define TWIC_LED_GPIO_LED3 (11)
#undef TWIC_LED_GPIO_LED4
#undef TWIC_LED_GPIO_LED5

#endif


/*
 * DEBUG UART definition. Undef it if you do not use TWiC DEBUG feature.
 *
 * Evaluation Board.
 *  TWIC_DEBUG_UART_NUMBER (TWIC_DEBUG_UART_CH_0)
 *
 * Reference Board.
 *  TWIC_DEBUG_UART_NUMBER (TWIC_DEBUG_UART_CH_1)
 *
 * Please check RTE_Device.h for the UART Channel.
 *
 */
#define TWIC_DEBUG_UART_CH_0   (0)
#define TWIC_DEBUG_UART_CH_1   (1)

/* TWIC_DEBUG_LOG_TRACE must be defined when DEBUG UART is used. */
#undef TWIC_DEBUG_LOG_TRACE /* enable trace log */

/* TWIC_DEBUG_LOG_TRACE must be defined when TWIC_DEBUG_LOG_UART is used. */
#undef TWIC_DEBUG_LOG_UART /* enable controller communication log */

#if defined(TWIC_DEBUG_LOG_TRACE)

#if defined(TZ1XXX_BOARD_DEVELOPER_A)
#define TWIC_DEBUG_UART_NUMBER (TWIC_DEBUG_UART_CH_0)
#endif

#if defined(TZ1XXX_BOARD_REFERENCE_A) || defined(TZ1XXX_BOARD_REFERENCE_B)
#define TWIC_DEBUG_UART_NUMBER (TWIC_DEBUG_UART_CH_1)
#endif

#if defined(TZ1XXX_BOARD_DEVELOPER_X) || defined(TZ1XXX_BOARD_EXTERIOR_A)
#define TWIC_DEBUG_UART_NUMBER (TWIC_DEBUG_UART_CH_0)
#endif

#else

#undef TWIC_DEBUG_UART_NUMBER

#endif


/*
 * DCDCEN.
 *
 * MCU_GPIO_1 controls BLE internal DCDCEN.
 *
 * Define TWIC_LECE_DCDCEN if use BLE internal DCDC.
 *
 * Please check RTE_Device.h for the GPIO.
 *
 */
#if defined(TZ1XXX_BOARD_DEVELOPER_A)

#undef  TWIC_LECE_DCDCEN
#define TWIC_LECE_DCDCEN_GPIO_NUMBER (1)

#elif defined(TZ1XXX_BOARD_REFERENCE_A) || defined(TZ1XXX_BOARD_REFERENCE_B)

#define TWIC_LECE_DCDCEN
#define TWIC_LECE_DCDCEN_GPIO_NUMBER (1)

#elif defined(TZ1XXX_BOARD_DEVELOPER_X) || defined(TZ1XXX_BOARD_EXTERIOR_A)

#undef TWIC_LECE_DCDCEN
#undef TWIC_LECE_DCDCEN_GPIO_NUMBER

#endif
  

/*
 * Define TWIC_LECE_LOWPOWER if use BLE low power mode.
 *
 * Choose the clock source of Low power.
 *
 */
#define TWIC_LECE_LOWPOWER

#if defined(TZ1XXX_BOARD_DEVELOPER_A)

#define TWIC_LECE_CLK32K_SLPXOIN_XTAL
#undef  TWIC_LECE_CLK32K_SLPXOIN_OSC32K_FROM_CG_CLK32K_OUT
#undef  TWIC_LECE_CLK32K_SLPXOIN_SIOSC32K_FROM_CG_CLK32K_OUT

#elif defined(TZ1XXX_BOARD_REFERENCE_A) || defined(TZ1XXX_BOARD_REFERENCE_B)

#define TWIC_LECE_CLK32K_SLPXOIN_CLOCK
#define TWIC_LECE_CLK32K_SLPXOIN_OSC32K_FROM_CG_CLK32K_OUT
#undef  TWIC_LECE_CLK32K_SLPXOIN_SIOSC32K_FROM_CG_CLK32K_OUT

#elif defined(TZ1XXX_BOARD_REFERENCE_X) || defined(TZ1XXX_BOARD_EXTERIOR_A)

#define TWIC_LECE_CLK32K_SLPXOIN_CLOCK
#define TWIC_LECE_CLK32K_SLPXOIN_OSC32K_FROM_CG_CLK32K_OUT
#undef  TWIC_LECE_CLK32K_SLPXOIN_SIOSC32K_FROM_CG_CLK32K_OUT

#endif


/*
 * TWIC_EXTERIOR.
 *
 * Please check RTE_Device.h for the GPIO.
 *
 */
#if defined(TZ1XXX_BOARD_EXTERIOR_A)

#define TWIC_LECE_SUPPRESS_HPD /* host program download mode */
#define TWIC_LECE_SUPPRESS_HPD_GPIO_NUMBER (26)

#else

#undef TWIC_LECE_SUPPRESS_HPD /* host program download mode */
#undef TWIC_LECE_SUPPRESS_HPD_GPIO_NUMBER

#endif

#endif /* _TZ1SM_CONFIG_H_ */
