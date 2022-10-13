const { validationResult } = require('express-validator');
const bcrypt = require('bcryptjs');
const { Op } = require('sequelize');
const jwt = require('jsonwebtoken');
const axios = require('axios');

const User = require('../models/user');

exports.guardRequest = (req, res, next) => {
    console.log(req.method + ": " + req.url + ", User: " + JSON.stringify(req.user));
    if (['GET', 'DELETE'].includes(req.method))
        return axios({
            method: req.method,
            url: 'http://localhost:9000' + req.url
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
            url: 'http://localhost:9000' + req.url,
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