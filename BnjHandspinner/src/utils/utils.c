/**
 * @file   utils.c
 * @brief  Utility functions.
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
#include <stdint.h>
/* MCU support. */
#include "TZ10xx.h"

#include "utils.h"

void Usleep(uint32_t usec)
{
  int32_t t, dt;
  if (SystemCoreClock >= 4000000) {
    t = ((SystemCoreClock + 999999) / 1000000) * usec;
    dt = 8;
  } else {
    t = usec * 100;
    dt = (8 * 100 * 1000000) / SystemCoreClock;
  }
  /* It assumes 8 cycles per loop */
  while (t > 0) {
    __NOP();
    __NOP();
    __NOP();
    __NOP();
    t -= dt;
  }
}
