const Sequelize = require('sequelize');

const sequelize = new Sequelize('documentdb', 'root', 'admin9786', {
    dialect: 'mysql',
    host: 'localhost'
});

module.exports = sequelize;