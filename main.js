// var http = require('http');
// var fs = require('fs');

// var PORT = 8080;

// fs.readFile('./main_page.html', function (err, html) {

//     if (err) throw err;    

//     http.createServer(function(request, response) {  
//         response.writeHeader(200, {"Content-Type": "text/html"});  
//         response.write(html);  
//         response.end();  
//     }).listen(PORT);
// });

const express = require('express');
const path = require('path');
const fs = require('fs');
const multer = require('multer')

const app = express();
const PORT = 8080;
const upload = multer({
    dest: "/tmp"
});

var image_counter = 0;


app.get('/', function(req, res) {
    res.sendFile(path.join(__dirname, '/main_page.html'));
  });

  const errorHandlingUpload = (res, err) => {
    res
    .status(500)
    .contentType("text/plain")
    .end("File was not successfully uploaded");
  }
app.post(
    "", upload.single("file"), (req, res) => {
        const tempPath = req.file.path;
        const targetPath = path.join(__dirname + '/images/map_' + image_counter + '.png');
        var target_value = image_counter;
        if (path.extname(req.file.originalname).toLowerCase() === ".png") {
            fs.rename(tempPath, targetPath, err => {
                if (err) return errorHandlingUpload(res, err);
                res.status(200).end("Image uploaded as map_" + target_value + ".png");
            })
        }
        image_counter++;
    }
)
app.use(express.static(__dirname + '/images'))
app.listen(PORT);
console.log('Server started at http://localhost:' + PORT);


