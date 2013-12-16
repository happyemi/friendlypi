#     This file is part of FriendlyPi.
# 
#     FriendlyPi is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     (at your option) any later version.
# 
#     FriendlyPi is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
# 
#     You should have received a copy of the GNU General Public License
#     along with FriendlyPi.  If not, see <http://www.gnu.org/licenses/>.

name = "mediadevice"
_path_to_directory = "/media/usb"

import os.path

def get_status():
	media_status = os.path.ismount(_path_to_directory)
	value = "Unmounted"
	actions =  ("Mount",)
	
	if media_status:
		value = "Mounted"
		actions = ("Unmount",)
			
	return {"text": "MediaDevice for " + _path_to_directory, "value": value, "actions": actions }


def exec_command(command):
	from subprocess import call
	if command == "Unmount":
		call(["umount", _path_to_directory])
	elif command == "Mount":
		call(["mount", _path_to_directory])


