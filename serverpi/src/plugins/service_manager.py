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

import os.path

class ServiceManager:

	def __init__(self, config):
		self._service_name = config["service"]
		self._pid_file = config["pid_file"]

	def get_status(self):
		service_running = os.path.isfile(self._pid_file)
		value = "Not Running"
		actions =  [{"id": "start", "label": "Start"}]
		
		if service_running:
			value = "Running"
			actions =  [{"id": "stop", "label": "Stop"}]
				
		return {"caption": "Service " + self._service_name, "status": value, "actions": actions }
	
	def exec_command(self, command):
		from subprocess import call
		if command == "start":
			call(["service", self._service_name, "start"])
		elif command == "stop":
			call(["service", self._service_name, "stop"])


