const { DataTypes } = require('sequelize');
const sequelize = require('../util/database');

const User = sequelize.define('users', {
    uid: {
        type: DataTypes.STRING,
        primaryKey: true
    },
    name: DataTypes.STRING,
    username: DataTypes.STRING,
    email: DataTypes.STRING,
    password: DataTypes.STRING
}, {
    timestamps: false,
    createdAt: false,
    updatedAt: false
});

module.exports = User;