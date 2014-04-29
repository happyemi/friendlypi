FriendlyPi
==========
 
FriendlyPi is a client/server solution that allows users to remote control their Linux box. Specifically, this project's goal is to allow the control of machine subsystems. For each subsystem, the server publishes the state and the list of actions that the user can undertake to interact with it. The client application can then be used to send the right commands to the server.
 
The typical use case is unmounting an external hard drive before disconnecting the USB cable on a keyboardless Linux box.  In this case, the subsystem is the hard drive, the published state can be either one of "mounted" or "unmounted", and the possible action can be either one of "Mount" or "Unmount", depending on the state. The user can use his tablet/phone to umount the drive without the need to remote login via ssh and use the console.

FriendlyPi server is plugin based and each plugin provides the code to handle a different subsystem. Hence, the user can write his own custom modules. 

Clients can use the browser to interact with the server, or use the Android app provided (downloadable from here: https://github.com/happyemi/friendlypi)
 
 
Install the server
------------------
 
The server can be installed using easy_install::

    easy_install3 FriendlyPi

NOTE: Do NOT use pip to install FriendlyPi, as the installation won't work properly. 
 
 
Setting up the server
---------------------
 
The server requires a configuration file to start properly. The config file must be named friendlypi.json and must be located in /etc/; it defines the list of modules' instances (i.e. the subsystems) that the server will handle. The simplest configuration is the following::

    {
    "instances": [["test1", "TestMod", {}]]
    }


This will instruct friendlypi to instantiate the testing module on startup.
The server runs on port 8080 by default. It's possible to set a different value (e.g. 80) like this:

    {
    "port": 80,
    "instances": [["test1", "TestMod", {}]]
    }


Testing the server
------------------

Once the server has been setup, start it manually. Start your preferred browser and point it to 127.0.0.1:8080/status?html=1  (of course replace the loopback address and the port with the real values, if different). If everything has been done properly, the browser should show you an instance of the test module running. An increment button is present and pressing it will increment the "Status" value.


Configure the MediaDevice module
--------------------------------

The MediaDevice module allows users to remotely mount/umount mount points. In order for this to work, /etc/fstab must be configured properly and the Unix user that runs the server must have the proper permissions to mount/umount the filesystem, Assuming the mountpoint is "/media/usb", friendlypi.json must look like this::

    {
    "instances": [["media1", "MediaDevice", {"path": "/media/usb"}]]
    }

"media1" is the instance name and it must be unique. "MediaDevice" is the module name and the last parameter is a map containing the configuration. It's possible to configure multiple mount points, like this::

    {
    "instances": [["media1", "MediaDevice", {"path": "/media/usb"}],
                  ["media2", "MediaDevice", {"path": "/media/usb2"}]]
    }


Configure the ServiceManager module
-----------------------------------

The ServiceManager module allows users to remotely start/stop services. In order for this to work the Unix user that runs the server must have the proper permissions to execute the "service" command, like this one::

    service samba start

Configuring a service manager, requires two things: the service name (e.g. "samba") and the pid file (e.g. "/var/run/samba/smbd.pid"). A valid configuration would look like this::

    {
    "instances": [["service1", "ServiceManager", {"service": "samba", "pid_file": "/var/run/samba/smbd.pid"}]]
    }

It's possible to configure multiple service managers, in the very same way described in the MediaDevice case

Disclaimer
----------

This software is in early alpha stage and doesn't implement any authentication mechanism, for now. Do NOT run the server on public networks. Use at your own risk.


Changes
=======

0.1d1
-----
- Server port is now configurable
- Added ServiceManager module
