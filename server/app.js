var fs = require('fs');
var express = require('express');
var multer = require('multer');

var app = express();

app.use('/uploads', express.static('uploads'));

var storage = multer.diskStorage({
    destination: function (reg, file, callback) {
        callback(null, './uploads');
    },

    filename: function (req, file, callback) {
        var fileFormat = (file.originalname).split(".");
        callback(null, file.fieldname + '_' + Date.now() + '.' + fileFormat[fileFormat.length - 1]);
    }
});

var upload = multer({
    storage: storage
}).single('photo');

app.get('/', function (req, res) {
    var form = fs.readFileSync('./upload.html', {encoding: 'utf8'});
    res.send(form);
})

app.get('/photos', function (req, res) {
    fs.readdir('./uploads', function (err, files) {
        if (err) {
            console.log(err);
            return;
        }
        //console.log(files);

        var result = files.reverse().map(function (file) {
            console.log(file);
            return {name: file, url: 'http://192.168.1.4:3000/uploads/' + file};
        })
        res.send(result);
    })
});

app.post('/upload', function (req, res, next) {
    upload(req, res, function (err) {
        if (err) {
            res.send({code: 0, message: 'error:' + err});
            return;
        }

        var file = req.file;
        res.send({code: 1, name: file.originalname, url: file.path });
    })

});

app.listen(3000, function () {
    console.log('Example app listening on port 3000!')
})
