const jwt = require('jsonwebtoken');
const User = require('../models/user');

module.exports = (req, res, next) => {
    const authHeader = req.get('Authorization');
    if (!authHeader) {
        const error = new Error('Not Authenticated');
        error.statusCode = 401;
        throw error;
    }
    const token = !authHeader.includes(' ') ? null : authHeader.split(' ')[1];//authHeader="Bearer <token>" and hence split(' ')[0] needs to be done to get the token//get() method - getting the Authorization Header!
    if (token == null) {//Not Authenticated!
        const error = new Error('Not Authenticated!');
        error.statusCode = 401;
        throw error;
    }
    let decodedToken;
    try {
        decodedToken = jwt.verify(token, 'longpasswordstringinprodforsecurity');//both decodes and verifies the token! Using .decode() only decodes the token!
    } catch (err) {
        err.statusCode = 500;
        throw err;
    }
    if (!decodedToken) {//Not Authenticated!
        const error = new Error('Not Authenticated!');
        error.statusCode = 401;
        throw error;
    }
    req.uid = decodedToken.uid;
    User.findByPk(req.uid).then(user => {
        if (!user) {
            const error = new Error('No user found with the given id: ' + req.uid);
            error.statusCode = 401;
            throw error;
        }
        req.user = user;
        next();
    }).catch(err => {
        if (!err.statusCode) {
            err.statusCode = 500;
        }
        next(err);
    });
};