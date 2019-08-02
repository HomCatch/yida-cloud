const express = require('express');
const proxy = require('http-proxy-middleware');

const app = express();
app.use('/', express.static(`${__dirname}/dist/`));

// api转发
app.use('/api', proxy({
    // target: 'http://10.0.1.238:9191',
    target: 'http://iotsvr.he-live:7001',
    changeOrigin: true,
    pathRewrite: {
        '^/api': ''
    }
}));

app.get('/*', (req, res) => {
    res.sendFile(`${__dirname}/dist/index.html`);
})

app.listen(7000, function () { });