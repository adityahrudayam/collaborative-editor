const mongoose = require("mongoose");

const userSchema = mongoose.Schema({
    uid: String,
    name: String,
    username: String,
    email: String,
    password: String
}, {
    timestamps: true,
    createdAt: true,
    updatedAt: true
});

module.exports = mongoose.model('User', userSchema);