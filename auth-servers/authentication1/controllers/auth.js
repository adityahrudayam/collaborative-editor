const { validationResult } = require('express-validator');
const bcrypt = require('bcryptjs');
const { Op } = require('sequelize');
const jwt = require('jsonwebtoken');
const axios = require('axios');

const User = require('../models/user');

exports.signup = (req, res, next) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        const error = new Error('Input Validation Failed!');
        error.statusCode = 422;
        error.data = errors.array();
        throw error;
    }
    const email = req.body.email;
    const name = req.body.name;
    const password = req.body.password;
    bcrypt.hash(password, 12).then(hashPassword => {
        return axios.post('http://localhost:8083/user-service/sign-up', {
            name: name,
            username: req.body.username,
            email: email,
            password: hashPassword
        });
    }).then(result => {
        console.log(result.data);
        res.status(201).json({
            message: 'User created successfully!',
            user: result.data
        });
    }).catch(err => {
        if (!err.statusCode) {
            err.statusCode = 500;
        }
        next(err);
    });
};

exports.login = (req, res, next) => {
    const email = "" + req.body.email;
    let isEmail = false;
    if (email.includes('@') && email.includes('.com'))
        isEmail = true;
    const password = req.body.password;
    let currentUser;
    if (isEmail) {
        User.findOne({
            where: {
                email: {
                    [Op.eq]: email
                }
            }
        }).then(user => {
            if (!user) {
                const error = new Error('No user found!');
                error.statusCode = 401;
                throw error;
            }
            currentUser = user;
            return bcrypt.compare(password, user.password);
        }).then(isEqual => {
            if (!isEqual) {
                const error = new Error('Incorrect Password!');
                error.statusCode = 401;
                throw error;
            }
            const token = jwt.sign({
                email: currentUser.email,
                uid: currentUser.uid
            }, 'longpasswordstringinprodforsecurity', { expiresIn: '4h' });
            res.status(200).json({
                token: token,
                uid: currentUser.uid,
                name: currentUser.name,
                username: currentUser.username,
                email: currentUser.email
            });
        }).catch(err => {
            if (!err.statusCode) {
                err.statusCode = 500;
            }
            next(err);
        });
    } else {
        User.findOne({
            where: {
                username: {
                    [Op.eq]: email
                }
            }
        }).then(user => {
            if (!user) {
                const error = new Error('No user found!');
                error.statusCode = 401;
                throw error;
            }
            currentUser = user;
            return bcrypt.compare(password, user.password);
        }).then(isEqual => {
            if (!isEqual) {
                const error = new Error('Incorrect Password!');
                error.statusCode = 401;
                throw error;
            }
            const token = jwt.sign({
                username: currentUser.username,
                uid: currentUser.uid
            }, 'longpasswordstringinprodforsecurity', { expiresIn: '4h' });
            res.status(200).json({
                token: token,
                uid: currentUser.uid,
                name: currentUser.name,
                username: currentUser.username,
                email: currentUser.email
            });
        }).catch(err => {
            if (!err.statusCode) {
                err.statusCode = 500;
            }
            next(err);
        });
    }
};

exports.guardRequest = (req, res, next) => {
    console.log(req.method + ": " + req.url + ", User: " + JSON.stringify(req.user));
    if (['GET', 'DELETE'].includes(req.method))
        return axios({
            method: req.method,
            url: 'http://localhost:8083' + req.url
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
            url: 'http://localhost:8083' + req.url,
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