'use strict';
const nodemailer = require('nodemailer');
const config = require('../../../config');

// create reusable transporter object using the default SMTP transport
let transporter = nodemailer.createTransport({
    service: config.email.service,
    auth: {
        user: config.email.address,
        pass: config.email.password
    }
});

module.exports = transporter;
