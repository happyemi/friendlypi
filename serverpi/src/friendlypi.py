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

def create_instances(config_file, modules):
	"""
	Reads config_file and returns a list of tuple with the following structure:
		[(name1, obj1), (name2, obj2)...]
	where 'name' is the name of the module instance, and 'obj' is an instance of the class 
	specified in the config file
	"""
	instances = []
	import json
	data = open(config_file)
	content = json.load(data)
	for instance_name, module_name, config in content:
		instances.append((instance_name, modules[module_name](config)))
	return instances

# Create module objects (instances)
mod_instances = create_instances("/etc/friendlypi.json", modules)

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
application.listen(8080)
tornado.ioloop.IOLoop.instance().start()
