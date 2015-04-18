var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var connectCounter = 0;
var socketMap = {};
var clientIPs = [];

app.get('/', function(req, res){
	res.sendFile(__dirname + '/index.html');
});

app.get('/private', function(req, res){
	console.log('/private');
	var clientIP = req.connection.remoteAddress;

	if(socketMap[clientIP]){
		res.send(socketMap[clientIP]);
		return;
	}

	var flag = true;
	for(var index in clientIPs){
		if(clientIPs[index] == clientIP)
			flag = false;
	}

	if(flag)
		clientIPs.push(clientIP);

	if(clientIPs.length < 2)
		res.send('false');

	if(clientIPs.length == 2){
		socketMap[clientIPs[0]] = clientIPs[1];
		socketMap[clientIPs[1]] = clientIPs[0];
		clientIPs = [];
		res.send(socketMap[clientIP]);
	}
});

io.on('connection', function(socket){
	connectCounter++;
	console.log('connect' + socket.handshake.address);
	socket.on('new user', function(){
    	io.emit('new user', connectCounter);
	});

	socket.on('new message', function(msg){
  		io.emit('new message', msg);
	});

	socket.on('new image', function(msg){
		console.log(msg);
  		io.emit('new image', msg);
	});

  	socket.on('disconnect', function(){ 
  		connectCounter--;
		var disconnectIP = socket.handshake.address;
		console.log('disconnect' + disconnectIP);
  		io.emit('disconnect', connectCounter); 
  	});
	
	socket.on('private', function(msg) {
  		var clientIP = socket.handshake.address;
  		var targetIP = socketMap[clientIP];
  		for(var index in io.sockets.sockets){
  			console.log('targetIP= ' + targetIP);
  			console.log('currentIP= ' + io.sockets.sockets[index].conn.remoteAddress);
  			if(io.sockets.sockets[index].conn.remoteAddress == targetIP){
  				console.log('true');
				console.log(msg);
  				io.sockets.sockets[index].emit('private', msg);
  				break;
  			}
		}
  	});
});


http.listen(3000, function(){
  console.log('listening on *:3000');
});
