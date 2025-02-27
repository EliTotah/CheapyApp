const express = require('express');
const router = express.Router();
const priceController = require('../controllers/price');

router.post('/getItemPriceByStore', priceController.getItemPriceByStore);
router.post('/getTotalPriceByStore', priceController.getTotalPriceByStore);

module.exports = router;