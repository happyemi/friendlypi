#     Copyright 2013-2014 Emiliano Mennucci
#
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

from friendlyutils.modutils import module
import os.path

@module
class MediaDevice:

	def __init__(self, params):
		self._path = params["path"]

	def get_status(self):
		media_status = os.path.ismount(self._path)
		value = "Unmounted"
		actions =  [{"id": "mount", "label": "Mount"}]
		
		if media_status:
			value = "Mounted"
			actions =  [{"id": "umount", "label": "Umount"}]
				
		return {"caption": "MediaDevice for " + self._path, "status": value, "actions": actions }
	
	def exec_command(self, command):
		from subprocess import call
		if command == "umount":
			call(["umount", self._path])
		elif command == "mount":
			call(["mount", self._path])


