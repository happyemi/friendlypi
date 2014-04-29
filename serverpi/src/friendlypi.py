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

import tornado.ioloop
import tornado.web

modules = {}
from pkg_resources import iter_entry_points, resource_filename, Requirement
for plugin in iter_entry_points('org.happyemi.friendlypi'):
	modules[plugin.name] = plugin.load()

def create_instances(settings, modules):
	"""
	Parse 'settings' and returns a list of tuples with the following structure:
		[(name1, obj1), (name2, obj2)...]
	where 'name' is the name of the module instance, and 'obj' is the instance
	"""
	instances = []
	for instance_name, module_name, config in settings:
		instances.append((instance_name, modules[module_name](config)))
	return instances

# Read config file
import json
config_file = open("/etc/friendlypi.json")
config = json.load(config_file)
config_file.close()
del config_file

# Create module objects (instances)
mod_instances = create_instances(config["instances"], modules)

# Set port
port = 8080
if 'port' in config:
	port = config["port"]

def get_time():
	import time
	return str(int(time.time() * 10))

def get_status(mod_instances):
	"Creates the JSON dictionary representing the status"
	
	ret = []
	for m, o in mod_instances:
		result = o.get_status()
		if not result:
			result = {}
		result["name"] = m
		ret.append(result)
	ret = {"data": ret, "version": 0}
	return ret

class PluginHandler(tornado.web.RequestHandler):
	"Handles plugin commands"		
	
	def initialize(self, mod_instance):
		self.mod_instance = mod_instance
		
	def get(self, command):
		self.mod_instance.exec_command(command)
		if self.get_argument("html", "0") == "1":
			self.redirect("/status?html=1&cachebuster=" + get_time(), True)
		else:
			self.write(get_status(mod_instances))
		
class StatusHandler(tornado.web.RequestHandler):
	"Handles status updates"
	
	def get(self):
		status = get_status(mod_instances)
		if self.get_argument("html", "0") == "1":
			self.render("index.html", plugins = status["data"], cachebuster = get_time())
		else:
			self.write(status)


def prepare_plugin_handlers():
	handlers = []
	for m, o in mod_instances:
		handlers.append((r"/command/" + m + r"/(.*)", PluginHandler, dict(mod_instance = o)))
	return handlers


html_path = resource_filename(Requirement.parse("FriendlyPi"), "html")
settings = { "template_path": html_path}

handlers = [(r"/status", StatusHandler)]
handlers.extend(prepare_plugin_handlers())
application = tornado.web.Application(handlers, **settings)
application.listen(port)
tornado.ioloop.IOLoop.instance().start()
