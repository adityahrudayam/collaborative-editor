const express = require('express');
const { body } = require('express-validator');
const { Op } = require('sequelize');

const User = require('../models/user');
const isAuth = require('../middleware/isAuth');
const authController = require('../controllers/auth');

const router = express.Router();

router.all('/edit-publisher/*', isAuth, authController.guardRequest);

module.exports = router;