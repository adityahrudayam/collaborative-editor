const express = require('express');
const { body } = require('express-validator');
const { Op } = require('sequelize');

const User = require('../models/user');
const isAuth = require('../middleware/isAuth');
const authController = require('../controllers/auth');

const router = express.Router();

router.post('/user-service/sign-up', [
    body('name')
        .trim()
        .notEmpty(),
    body('username')
        .trim()
        .notEmpty()
        .custom((value) => {
            return User.findOne({
                where: {
                    username: {
                        [Op.eq]: value
                    }
                }
            }).then(user => {
                if (user) {
                    return Promise.reject('Username already exists! ');
                }
            }).catch(err => {
                if (!err.statusCode) {
                    err.statusCode = 500;
                }
                next(err);
            });
        }),
    body('email')
        .isEmail()
        .withMessage('Please Enter a valid Email!')
        .custom((value) => {
            return User.findOne({
                where: {
                    email: {
                        [Op.eq]: value
                    }
                }
            }).then(user => {
                if (user) {
                    return Promise.reject('Email already exists! ');
                }
            }).catch(err => {
                if (!err.statusCode) {
                    err.statusCode = 500;
                }
                next(err);
            });
        }).normalizeEmail(),
    body('password')
        .trim()
        .isLength({ min: 6 })
], authController.signup);

router.post('/user-service/login', authController.login);

router.all('/user-service/*', isAuth, authController.guardRequest);

router.all('/document-service/*', isAuth, authController.guardRequest);

router.all('/edit-publisher/*', isAuth, authController.guardRequest);


module.exports = router;