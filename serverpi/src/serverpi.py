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

# Import plugins files
plugins_dir = "plugins"
import importlib
importlib.import_module(plugins_dir)

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
	for name, module, params in content:
		pymod_name, class_name = module.split(".")
		pymod = importlib.import_module(plugins_dir + "." + pymod_name)
		class_obj = getattr(pymod, class_name)
		instances.append((name, class_obj(params)))
	return instances

# Create module objects (instances)
import friendlyutils.modutils
mod_instances = create_instances("friendlypi.json", friendlyutils.modutils.mod_list)

class PluginHandler(tornado.web.RequestHandler):
	"Handles plugin commands"		
	
	def initialize(self, mod_instance):
		self.mod_instance = mod_instance
		
	def get(self, command):
		self.mod_instance.exec_command(command)
		self.redirect("/status", True)
		
class StatusHandler(tornado.web.RequestHandler):
	"Handles status updates"
	
	def get(self):
		ret = []
		for m, o in mod_instances:
			result = o.get_status()
			if not result:
				result = {}
			result["name"] = m
			ret.append(result)
		self.render("index.html", plugins = ret)


def prepare_plugin_handlers():
	handlers = []
	for m, o in mod_instances:
		handlers.append((r"/command/" + m + r"/(.*)", PluginHandler, dict(mod_instance = o)))
	return handlers

settings = { "template_path": "../html/" }

handlers = [(r"/status", StatusHandler)]
handlers.extend(prepare_plugin_handlers())
application = tornado.web.Application(handlers, **settings)
application.listen(8080)
tornado.ioloop.IOLoop.instance().start()