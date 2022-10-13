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
        if (req.method === 'POST' && req.url === '/user-service/share-access-to-user' && user.uid !== req.body?.ownerUid)
            throw new Error("Unauthorized Request! Please check & try again.");
        if (req.method === 'POST' && req.url === '/user-service/share-access-to-users' && user.uid !== req.body?.owner)
            throw new Error("Unauthorized Request! Please check & try again.");
        if (req.method === 'POST' && req.url === '/user-service/share-access-to-users-by-username' && user.username !== req.body?.owner)
            throw new Error("Unauthorized Request! Please check & try again.");
        if (req.method === 'POST' && req.url === '/user-service/share-access-to-users-by-email' && user.email !== req.body?.owner)
            throw new Error("Unauthorized Request! Please check & try again.");
        // if (req.method === 'POST' && req.url === '/document-service/upload-json' && user.uid !== req.body?.uid)
        //     throw new Error("Unauthorized Request! Please check & try again.");
        // if (req.method === 'PUT' && req.url === '/document-service/update-document-name' && user.uid !== req.body?.uid)
        //     throw new Error("Unauthorized Request! Please check & try again.");
        // if (req.method === 'PUT' && req.url === '/document-service/change-edit-status' && user.uid !== req.body?.ownerUid)
        //     throw new Error("Unauthorized Request! Please check & try again.");
        // if (req.method === 'PUT' && req.url === '/document-service/stop-edit-save-status' && user.uid !== req.body?.ownerUid)
        //     throw new Error("Unauthorized Request! Please check & try again.");
        if (!(req.url.includes('/users-by-username-search') || req.url.includes('/users-by-email-search') || req.url.includes('/users-by-username-filter') || req.url.includes('/users-by-email-filter'))) {
            if (req.method === 'PUT' && req.url.includes('/user-service/edit-user/')) {
                const urlSegments = req.url.split("/");
                const checkUid = urlSegments[urlSegments.length - 1];
                if (checkUid !== user.uid)
                    throw new Error("Unauthorized Request! Please check & try again.");
            } else if (req.method === 'GET' || req.method === 'DELETE') {
                if (req.url.includes('username/')) {
                    const urlSegments = req.url.split("/");
                    const checkUsername = urlSegments[urlSegments.length - 1];
                    if (checkUsername !== user.username)
                        throw new Error("Unauthorized Request! Please check & try again.");
                } else if (req.url.includes('email/')) {
                    const urlSegments = req.url.split("/");
                    const checkEmail = urlSegments[urlSegments.length - 1];
                    if (checkEmail !== user.email)
                        throw new Error("Unauthorized Request! Please check & try again.");
                } else {
                    const urlSegments = req.url.split("/");
                    const checkUid = urlSegments[urlSegments.length - 1];
                    if (checkUid !== user.uid)
                        throw new Error("Unauthorized Request! Please check & try again.");
                }
            }
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