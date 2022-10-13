const express = require('express');
const bodyParser = require('body-parser');
const eurekaHelper = require('./eureka-helper.js');

eurekaHelper.registerWithEureka('auth-server1', 7001);

const sequelize = require('./util/database');
const authRoutes = require('./routes/auth');

const app = express();

app.use(bodyParser.json());

app.use((req, res, next) => { //CORS Headers!
    res.setHeader('Allow-Control-Allow-Origin', '*');
    res.setHeader('Allow-Control-Allow-Methods', 'OPTIONS,POST,GET,PUT,PATCH,DELETE');
    res.setHeader('Allow-Control-Allow-Headers', 'Content-Type, Authorization');
    next();
});

app.use(authRoutes);

app.use('/', (err, req, res, next) => {//Error handling middleware to catch all errors!
    console.log(err);
    const status = err.statusCode || 500;
    const message = err?.response?.data?.['Error msg'] || err.message || "An error occurred. Please check the request & try again.";
    return res.status(status).json({
        error: message
    });
});

sequelize.sync().then(() => {
    app.listen(7001);
    console.log("server started");
}).catch(err => console.log(err));