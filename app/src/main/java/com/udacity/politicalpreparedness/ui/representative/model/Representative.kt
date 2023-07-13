package com.udacity.politicalpreparedness.ui.representative.model

import com.udacity.politicalpreparedness.data.network.models.Office
import com.udacity.politicalpreparedness.data.network.models.Official

data class Representative(val official: Official, val office: Office)
