const axios = require('axios');

exports.guardRequest = (req, res, next) => {
    console.log(req.method + ": " + req.url + ", User: " + JSON.stringify(req.user));
    if (['GET', 'DELETE'].includes(req.method))
        return axios({
            method: req.method,
            url: 'http://localhost:8082' + req.url
        }).then(result => {
            return res.status(200).json({
                response: result.data
            });
        }).catch(err => {
            if (!err.statusCode) {
                err.statusCode = 500;
            }
            // console.log(err?.response?.data);
            next(err);
        });
    else
        return axios({
            method: req.method,
            url: 'http://localhost:8082' + req.url,
            data: req.body
        }).then(result => {
            return res.status(200).json({
                response: result.data
            });
        }).catch(err => {
            if (!err.statusCode) {
                err.statusCode = 500;
            }
            // console.log(err?.response?.data);
            next(err);
        });
};