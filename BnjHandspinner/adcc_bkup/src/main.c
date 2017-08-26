/**
 * @file   main.c
 * @brief  Application main.
 *
 * @author Cerevo Inc.
 */

/*
Copyright 2015 Cerevo Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdint.h>

#include "TZ10xx.h"
#include "PMU_TZ10xx.h"
#include "ADCC12_TZ10xx.h"
#include "SDMAC_TZ10xx.h"

#include "TZ01_system.h"
#include "TZ01_console.h"

#include "utils.h"

extern TZ10XX_DRIVER_PMU  Driver_PMU;
extern TZ10XX_DRIVER_ADCC12 Driver_ADCC12;

void init_adcc12(void)
{
    /* ADCC12Ö·éNbNÌİè */
    //NbN\[XÌIğ
    Driver_PMU.SelectClockSource(PMU_CSM_ADCC12, PMU_CLOCK_SOURCE_SIOSC4M);
    //vXP[Ìİè
    Driver_PMU.SetPrescaler(PMU_CD_ADCC12, 1);

    /* ADCC12hCoÌú» */
    Driver_ADCC12.Initialize(NULL, 0);

    Driver_ADCC12.SetScanMode(ADCC12_SCAN_MODE_CYCLIC, ADCC12_CHANNEL_0);
    Driver_ADCC12.SetScanMode(ADCC12_SCAN_MODE_CYCLIC, ADCC12_CHANNEL_1);
    Driver_ADCC12.SetScanMode(ADCC12_SCAN_MODE_CYCLIC, ADCC12_CHANNEL_2);
    Driver_ADCC12.SetScanMode(ADCC12_SCAN_MODE_CYCLIC, ADCC12_CHANNEL_3);

    Driver_ADCC12.SetDataFormat(ADCC12_CHANNEL_0, ADCC12_UNSIGNED);
    Driver_ADCC12.SetDataFormat(ADCC12_CHANNEL_1, ADCC12_UNSIGNED);
    Driver_ADCC12.SetDataFormat(ADCC12_CHANNEL_2, ADCC12_UNSIGNED);
    Driver_ADCC12.SetDataFormat(ADCC12_CHANNEL_3, ADCC12_UNSIGNED);

    Driver_ADCC12.SetComparison(ADCC12_CMP_DATA_0, 0x00, ADCC12_CMP_NO_COMPARISON, ADCC12_CHANNEL_0);
    Driver_ADCC12.SetComparison(ADCC12_CMP_DATA_0, 0x00, ADCC12_CMP_NO_COMPARISON, ADCC12_CHANNEL_1);
    Driver_ADCC12.SetComparison(ADCC12_CMP_DATA_0, 0x00, ADCC12_CMP_NO_COMPARISON, ADCC12_CHANNEL_2);
    Driver_ADCC12.SetComparison(ADCC12_CMP_DATA_0, 0x00, ADCC12_CMP_NO_COMPARISON, ADCC12_CHANNEL_3);

    Driver_ADCC12.SetFIFOOverwrite(ADCC12_FIFO_MODE_STREAM);
    Driver_ADCC12.SetSamplingPeriod(ADCC12_SAMPLING_PERIOD_64MS);

    Driver_ADCC12.PowerControl(ARM_POWER_FULL);
}

uint16_t adcc12_0, adcc12_1, adcc12_2, adcc12_3;
uint8_t msg[80];

int main(void)
{
    /* Initialize */
    TZ01_system_init();
    TZ01_console_init();

    init_adcc12();  //12bit ADCÌú»
    Driver_ADCC12.Start();

    TZ01_system_tick_start(USRTICK_NO_GPIO_INTERVAL, 1000);

    for (;;) {
        if (TZ01_system_run() == RUNEVT_POWOFF) {
            /* Power off operation detected */
            break;
        }

        /* PbÉADCC12ğÇŞ */
        if (TZ01_system_tick_check_timeout(USRTICK_NO_GPIO_INTERVAL)) {
            TZ01_system_tick_start(USRTICK_NO_GPIO_INTERVAL, 1000);
            //Ch0
            Driver_ADCC12.ReadData(ADCC12_CHANNEL_0, &adcc12_0);
            //Ch1
            Driver_ADCC12.ReadData(ADCC12_CHANNEL_1, &adcc12_1);
            //Ch2
            Driver_ADCC12.ReadData(ADCC12_CHANNEL_2, &adcc12_2);
            //Ch3
            Driver_ADCC12.ReadData(ADCC12_CHANNEL_3, &adcc12_3);

            sprintf(
                msg, "ADCC12: CH0=%d CH1=%d CH2=%d CH3=%d\r\n",
                adcc12_0, adcc12_1, adcc12_2, adcc12_3
            );
            TZ01_console_puts(msg);
        }
    }

    TZ01_console_puts("Program terminated.\r\n");
    return 0;
}
