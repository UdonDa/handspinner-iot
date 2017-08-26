/**
 * @file config_blelib.h
 * @brief configuration for BLELib
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

//-------- <<< Use Configuration Wizard in Context Menu >>> --------------------

#include "twic_interface.h"

#ifndef _CONFIG_BLELIB_H_
#define _CONFIG_BLELIB_H_

#ifdef __cplusplus
extern "C" {
#endif

// <h> BLELib configuration
//   <q> enable debug print
#define BLELIB_DEBUG_PRINT (1)

//   <o> maximum attribute number <1-255>
#define BLELIB_ENTRY_SIZE_MAX 	(40)
#if (BLELIB_ENTRY_SIZE_MAX < 1) || (BLELIB_ENTRY_SIZE_MAX > 255)
#error BLELIB_ENTRY_SIZE_MAX must be in range from 1 to 255
#endif
	
//   <o> inner buffer number <1-255>
#define BLELIB_INNER_BUF_NUM (8)
#if (BLELIB_INNER_BUF_NUM < 1) || (BLELIB_INNER_BUF_NUM > 255)
#error BLELIB_INNER_BUF_NUM must be in range from 1 to 255
#endif

//	<o>	multiple read maximum number <1-255>
#define BLELIB_MULTIPLE_READ_MAX_NUM	(10)
#if (BLELIB_MULTIPLE_READ_MAX_NUM < 1) || (BLELIB_MULTIPLE_READ_MAX_NUM > 255)
#error BLELIB_MULTIPLE_READ_MAX_NUM must be in range from 1 to 255
#endif

//   <o> punctuation of UART2(us) <3-8192>
#define BLELIB_UART2_PUNCTUATION (12)
#if (BLELIB_UART2_PUNCTUATION < 3) || (BLELIB_UART2_PUNCTUATION > 8192)
#error BLELIB_UART2_PUNCTUATION must be in range from 3 to 8192
#endif
// </h>

// <h> Advertising parameter
//   <o> minimum interval (n * 0.625ms) <0x20-0x4000>
#define BLELIB_MIN_ADVERTISING_INTERVAL (0x0320)
#if (BLELIB_MIN_ADVERTISING_INTERVAL < 0x20) || (BLELIB_MIN_ADVERTISING_INTERVAL > 0x4000)
#error BLELIB_MIN_ADVERTISING_INTERVAL must be in range from 0x20 to 0x4000
#endif
//   <o> maximum interval (n * 0.625ms) <0x20-0x4000>
#define BLELIB_MAX_ADVERTISING_INTERVAL (0x0320)
#if (BLELIB_MAX_ADVERTISING_INTERVAL < 0x20) || (BLELIB_MAX_ADVERTISING_INTERVAL > 0x4000)
#error BLELIB_MAX_ADVERTISING_INTERVAL must be in range from 0x20 to 0x4000
#endif
//   <o> Advertising type
//     <0=>Connectable advertising
//     <2=>Scannable advertising
//     <3=>Non connectable advertising
#define BLELIB_ADVERTISING_TYPE_NUM (0)
#if BLELIB_ADVERTISING_TYPE_NUM == 0
#define BLELIB_ADVERTISING_TYPE TWIC_ADV_TYPE_IND
#elif BLELIB_ADVERTISING_TYPE_NUM == 2
#define BLELIB_ADVERTISING_TYPE TWIC_ADV_TYPE_SCAN_IND
#elif BLELIB_ADVERTISING_TYPE_NUM == 3
#define BLELIB_ADVERTISING_TYPE TWIC_ADV_TYPE_NONCONN_IND
#else 
#error Not supported Advertising type
#endif
//   <h> Advertising channel
//     <q> Channel 37
#define BLELIB_ADVERTISING_CHANNEL_37 (1)
//     <q> Channel 38
#define BLELIB_ADVERTISING_CHANNEL_38 (1)
//     <q> Channel 39
#define BLELIB_ADVERTISING_CHANNEL_39 (1)
//   </h>
#if BLELIB_ADVERTISING_CHANNEL_37 == 0 && BLELIB_ADVERTISING_CHANNEL_38 == 0 && BLELIB_ADVERTISING_CHANNEL_39 == 0
#error At least one channel must be specified
#endif
// </h>
	
	
// <h> Connection parameter
//   <o> minimum connection interval (n * 1.25ms) <6-3200>
#define BLELIB_MIN_CONNECTION_INTERVAL (6)
#if (BLELIB_MIN_CONNECTION_INTERVAL < 6) || (BLELIB_MIN_CONNECTION_INTERVAL > 3200)
#error BLELIB_MIN_CONNECTION_INTERVAL must be in range from 6 to 3200
#endif
//   <o> maximum connection interval (n * 1.25ms) <6-3200>
#define BLELIB_MAX_CONNECTION_INTERVAL (9)
#if (BLELIB_MAX_CONNECTION_INTERVAL < 6) || (BLELIB_MAX_CONNECTION_INTERVAL > 3200)
#error BLELIB_MAX_CONNECTION_INTERVAL must be in range from 6 to 3200
#endif
//   <o> slave latency <0-499>
#define BLELIB_SLAVE_LATENCY (4)
#if (BLELIB_SLAVE_LATENCY < 0) || (BLELIB_SLAVE_LATENCY > 499)
#error BLELIB_SLAVE_LATENCY must be in range from 0 to 499
#endif
//   <o> supervision timeout (n * 10ms) <10-3200>
#define BLELIB_SUPERVISION_TIMEOUT (400)
#if (BLELIB_SUPERVISION_TIMEOUT < 10) || (BLELIB_SUPERVISION_TIMEOUT > 3200)
#error BLELIB_SUPERVISION_TIMEOUT must be in range from 10 to 3200
#endif
//
// </h>



// <<< end of configuration section >>>

#ifdef __cplusplus
}
#endif

#endif /* _CONFIG_BLELIB_H_ */
