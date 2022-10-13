const express = require('express');

const isAuth = require('../middleware/isAuth');
const authController = require('../controllers/auth');

const router = express.Router();

router.all('/document-service/*', isAuth, authController.guardRequest);

module.exports = router;