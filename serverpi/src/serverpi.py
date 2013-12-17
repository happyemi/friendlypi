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

import plugins

class PluginHandler(tornado.web.RequestHandler):
	"Handles plugin commands"		
	
	def initialize(self, plugin):
		self.plugin = plugin
		
	def get(self, command):
		self.plugin.exec_command(command)
		self.redirect("/status", True)
		
class StatusHandler(tornado.web.RequestHandler):
	"Handles status updates"
	
	def get(self):
		ret = []
		for plugin in plugins.plugin_list:
			result = plugin.get_status()
			if not result:
				result = {}
			result["name"] = plugin.name
			ret.append(result)
		self.render("index.html", plugins = ret)


def prepare_plugin_handlers():
	handlers = []
	for plugin_name in plugins.plugin_map:
		handlers.append((r"/command/" + plugin_name + r"/(.*)", PluginHandler, dict(plugin = plugins.plugin_map[plugin_name])))
	return handlers

settings = { "template_path": "../html/" }

handlers = [(r"/status", StatusHandler)]
handlers.extend(prepare_plugin_handlers())
application = tornado.web.Application(handlers, **settings)
application.listen(8080)
tornado.ioloop.IOLoop.instance().start()