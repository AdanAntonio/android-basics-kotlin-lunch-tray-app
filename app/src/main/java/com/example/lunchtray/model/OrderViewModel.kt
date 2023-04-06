/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.lunchtray.data.DataSource
import java.text.NumberFormat

class OrderViewModel : ViewModel() {

    // Map of menu items
    val menuItems = DataSource.menuItems

    // Default values for item prices
    private var previousEntreePrice: Double = 0.0
    private var previousSidePrice = 0.0
    private var previousAccompanimentPrice = 0.0

    // Default tax rate
    private val taxRate = 0.08

    // Entree for the order
    private val _entree = MutableLiveData<MenuItem?>()
    val entree: LiveData<MenuItem?> = _entree

    // Side for the order
    private val _side = MutableLiveData<MenuItem?>()
    val side: LiveData<MenuItem?> = _side

    // Accompaniment for the order.
    private val _accompaniment = MutableLiveData<MenuItem?>()
    val accompaniment: LiveData<MenuItem?> = _accompaniment

    // Subtotal for the order
    private val _subtotal = MutableLiveData(0.0)
    val subtotal: LiveData<String> = Transformations.map(_subtotal) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    // Total cost of the order
    private val _total = MutableLiveData(0.0)
    val total: LiveData<String> = Transformations.map(_total) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    // Tax for the order
    private val _tax = MutableLiveData(0.0)
    val tax: LiveData<String> = Transformations.map(_tax) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    /**
     * Set the entree for the order.
     */
    fun setEntree(entree: String) {
        // TODO: if _entree.value is not null, set the previous entree price to the current
        //  entree price.
        var currentEntreePrice = 0.0
        if (_entree.value != null) {
            // previousEntreePrice = _entree.value?.price ?: 0.0
            _entree.value?.let {
                previousEntreePrice = it.price
            }
        }

        // TODO: if _subtotal.value is not null subtract the previous entree price from the current
        //  subtotal value. This ensures that we only charge for the currently selected entree.
        if (_subtotal.value != null) {
            _subtotal.value?.let {
                _subtotal.value = it - previousEntreePrice
            }
        }

        // TODO: set the current entree value to the menu item corresponding to the passed in string
        menuItems.forEach {
            if (it.key == entree) {
                Log.d("orderViewModel", "setEntree entree=$entree price=${it.value.price}")
                previousEntreePrice = it.value.price
                _entree.value = it.value
            }
        }
        // TODO: update the subtotal to reflect the price of the selected entree.
        updateSubtotal(previousEntreePrice)
    }

    /**
     * Set the side for the order.
     */
    fun setSide(side: String) {
        // TODO: if _side.value is not null, set the previous side price to the current side price.
        var currentSidePrice = 0.0
        if (_side.value != null) {
            currentSidePrice = previousSidePrice
        }

        // TODO: if _subtotal.value is not null subtract the previous side price from the current
        //  subtotal value. This ensures that we only charge for the currently selected side.
        if (_subtotal.value != null) {
            _subtotal.value?.let {
                _subtotal.value = it - previousSidePrice
            }
        }

        // TODO: set the current side value to the menu item corresponding to the passed in string
        menuItems.forEach {
            if (it.key == side) {
                previousSidePrice = it.value.price
                _side.value = it.value
            }
        }
        // TODO: update the subtotal to reflect the price of the selected side.
        updateSubtotal(previousSidePrice)
    }

    /**
     * Set the accompaniment for the order.
     */
    fun setAccompaniment(accompaniment: String) {
        // TODO: if _accompaniment.value is not null, set the previous accompaniment price to the
        //  current accompaniment price.
        var currentSidePrice = 0.0
        if (_accompaniment.value != null) {
            currentSidePrice = previousAccompanimentPrice
        }

        // TODO: if _accompaniment.value is not null subtract the previous accompaniment price from
        //  the current subtotal value. This ensures that we only charge for the currently selected
        //  accompaniment.
        if (_accompaniment.value != null) {
            _subtotal.value?.let {
                _subtotal.value = it - previousAccompanimentPrice
            }
        }

        // TODO: set the current accompaniment value to the menu item corresponding to the passed in
        //  string
        menuItems.forEach {
            if (it.key == accompaniment) {
                previousAccompanimentPrice = it.value.price
                _accompaniment.value = it.value
            }
        }
        // TODO: update the subtotal to reflect the price of the selected accompaniment.
        updateSubtotal(previousAccompanimentPrice)
    }

    /**
     * Update subtotal value.
     */
    private fun updateSubtotal(itemPrice: Double) {
        // TODO: if _subtotal.value is not null, update it to reflect the price of the recently
        //  added item.
        //  Otherwise, set _subtotal.value to equal the price of the item.
        if (_subtotal.value != null) {
            _subtotal.value?.let {
                _subtotal.value = it + itemPrice
            }
        } else {
            _subtotal.value = itemPrice
        }
        Log.d("orderViewModel", "updateSubtotal itemPrice=$itemPrice subTotal=${_subtotal.value}")

        // TODO: calculate the tax and resulting total
        calculateTaxAndTotal()
    }

    /**
     * Calculate tax and update total.
     */
    fun calculateTaxAndTotal() {
        // TODO: set _tax.value based on the subtotal and the tax rate.
        _subtotal.value?.let {
            _tax.value = it.times(taxRate)
        }
        // TODO: set the total based on the subtotal and _tax.value.
        _subtotal.value?.let { subtotal ->
            _tax.value?.let { tax ->
                _total.value = subtotal.plus(tax)
            }
        }
    }

    /**
     * Reset all values pertaining to the order.
     */
    fun resetOrder() {
        // TODO: Reset all values associated with an order
        _tax.value = 0.0
        _subtotal.value = 0.0
        _total.value = 0.0
        previousEntreePrice = 0.0
        previousSidePrice = 0.0
        previousAccompanimentPrice = 0.0
        _entree.value = null
        _side.value = null
        _accompaniment.value = null
    }
}
