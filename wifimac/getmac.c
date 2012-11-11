/*
 * Copyright (C) 2011 The CyanogenMod Project
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

#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

/* Read plain address retrieved by RIL and set the Wifi MAC 
 * address accordingly */

int main() {
	int fd1, fd2;
	int macbyte;
	int i;

	fd1 = open("/data/misc/wifi/macAddress",O_RDONLY);
	fd2 = open("/data/misc/wifi/config",O_WRONLY|O_CREAT|O_TRUNC,S_IRUSR|S_IWUSR|S_IRGRP|S_IROTH);

	write(fd2,"cur_etheraddr=",14);

	for (i = 0; i<12; i++) {
		lseek(fd1,i,SEEK_SET);
		lseek(fd2,0,SEEK_END);
		read(fd1,&macbyte,1);
		if (i>1 && !(i%2))
			write(fd2,":",1);
		write(fd2,&macbyte,1);
	}

	write(fd2,"\n",1);
	close(fd1);
	close(fd2);
	return 0;
}
