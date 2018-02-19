
const  http = require('http'),
	   https = require('https'),
	   url = require('url'),
	   fs = require('fs');

let app = http.createServer(function (req, res) {
  	const requestUrl = url.parse(req.url, true);
  	const action = requestUrl.pathname;

	if (req.method !== 'GET') {
	    res.writeHead(404, {'Content-Type': 'text/plain' });
	    res.end('404');
	    return;
  	}

	if (action === '/') {
	  res.writeHead(200, {'Content-Type': 'text/plain' });
	  res.end();
	  return;
	} 

    else if(action === '/image') {
        const imageUrl = requestUrl.query.url;

        if(imageUrl === undefined) {
            res.writeHead(404, {'Content-Type': 'text/plain' });
            res.end('400 Bad Request');
            return;
        }

        const imageUrlProtocol = url.parse(imageUrl, true).protocol;
        const extension = imageUrl.split('.').pop();
        const contentType = `image/${extension}`;
        
        let getMethod;

        if(imageUrlProtocol === 'https:') {
            getMethod = https.get;
        } else if(imageUrlProtocol === 'http:') {
            getMethod = http.get;
        } else {
            res.writeHead(400, {'Content-Type': 'text/plain' });
            res.end('400: Unsupported protocol');
            return;
        }

        getMethod(imageUrl, function(remoteImageRequest) {
            var imageData = [];

            remoteImageRequest.on('data', function(chunk) {
                imageData.push(chunk);
            });

            remoteImageRequest.on('end', function() {
                const buffer = Buffer.concat(imageData);

                res.writeHead(200, {'Content-Type': contentType});

                res.end(buffer);
            });
        });
    }

    else {
        res.writeHead(404, {'Content-Type': 'text/plain' });
        res.end('404');
    }
}).listen(5000);

console.log('Server running at http://localhost:5000/');
