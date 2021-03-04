package com.reachfree.dailyexpense.util

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import com.reachfree.dailyexpense.R
import com.reachfree.dailyexpense.data.model.Category
import com.reachfree.dailyexpense.data.model.Currency
import com.reachfree.dailyexpense.data.model.SubCategory

/**
 * DailyExpense
 * Created by Kim Yongdae
 * Date: 2021-02-15
 * Time: 오전 11:03
 */
object Constants {

    var currencySymbol = Currency.USD.symbol
    var percent = "%"

    const val FIRST_DAY_OF_WEEK = 1
    const val CHART_ANIMATION_SPEED = 1000
    const val ANIMATION_DURATION = 1000L

    // Preference
    const val PREF_KEY_IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH"
    const val PREF_KEY_CURRENCY_CODE = "CURRENCY"
    const val PREF_KEY_NICKNAME = "NICKNAME"
    const val PREF_KEY_FULLNAME = "FULLNAME"

    // Database
    const val LOCAL_DATABASE_NAME = "daily_expense"
    const val PERCENT_UNIT = "%"

    const val TODAY = 0
    const val YESTERDAY = 1
    const val PICK_DATE = 2

    const val TOTAL_TAB = 0
    const val EXPENSE_TAB = 1
    const val INCOME_TAB = 2

    const val EXPENSE_PATTERN_TOTAL = 0
    const val EXPENSE_PATTERN_NORMAL = 1
    const val EXPENSE_PATTERN_WASTE = 2
    const val EXPENSE_PATTERN_INVEST = 3

    const val TYPE_EXPENSE = "expense"
    const val TYPE_INCOME = "income"

    const val SALARY = ":salary"
    private const val INTEREST_DIVIDEND = ":interest_dividend"
    private const val BONUS = ":bonus"
    private const val SALE = ":sale"
    private const val REFUND = ":refund"
    private const val OTHER_INCOME = ":other_income"

    /**
     * Expense Category
     */
    const val FOOD_DRINKS = ":food_drinks"
    private const val SHOPPING = ":shopping"
    private const val PERSONAL_CARE = ":personal_care"
    private const val TRANSPORTATION = ":transportation"
    private const val COMMUNICATION = ":communication"
    private const val ENTERTAINMENT = ":entertainment"
    private const val HOUSEHOLD = ":household"
    private const val UTILITY = ":utility"
    private const val HEALTH_CARE = ":health_care"
    private const val EDUCATION = ":education"
    private const val TRAVEL = ":travel"
    private const val GIFTS_DONATION = ":gifts&donation"
    private const val PET = ":pet"
    private const val KIDS = ":kids"
    private const val INSURANCE = ":insurance"
    private const val TAXES = ":taxes"
    private const val OTHER_EXPENSE = ":other_expense"

    /**
     * Expense SubCategory
     */
    // Food & Drinks
    const val EATING_OUT = ":eatingout"
    private const val GROCERY = ":grocery"
    private const val SNACK = ":snack"
    private const val COFFEE = ":coffee"
    private const val BEVERAGE = "beverage"
    private const val OTHER = ":other"

    // Shopping
    private const val CLOTHING = ":clothing"
    private const val SHOES = ":shoes"
    private const val ACCESSORY = ":accessory"

    // Personal Care
    private const val HAIRCUT = ":haircut"
    private const val BEAUTY = ":beauty"
    private const val COSMETICS = ":cosmetics"
    private const val GYM = ":gym"
    private const val MASSAGE_SPA = ":massage_spa"

    // Transportation
    private const val METRO = ":metro"
    private const val BUS = ":bus"
    private const val TAXI = ":taxi"
    private const val CAR_GASFUEL = ":car_gasfuel"

    // Communication
    private const val MOBILE = ":mobile"
    private const val INTERNET_TV = ":internet_tv"
    private const val COM_SUBSCRIPTIONS = ":subscriptions"

    // Entertainment
    private const val MOVIE = ":movie"
    private const val PERFORMANCE = ":performance"
    private const val GAME = ":game"
    private const val HOBBIES = ":hobbies"

    // Household
    private const val HOME_APPLIANCES = ":home_appliances"
    private const val FURNITURE_DECOR = ":furniture_decor"
    private const val KITCHEN = ":kitchen"
    private const val CLEANING_SUPPLIES = ":cleaning_supplies"
    private const val LAUNDRY_MENDING = ":laundry_mending"

    // Utility
    private const val HOUSE_RENT = ":house_rent"
    private const val MAINTENANCE_FEE = ":maintenance_fee"
    private const val ELECTRIC = ":electric"
    private const val WATER = ":water"
    private const val UTILITY_GAS = ":utility_gas"

    // Health Care
    private const val HOSPITAL = ":hospital"
    private const val MEDICINE = ":medicine"
    private const val VITAMIN = ":vitamin"

    // Education
    private const val TUITION = ":tuition"
    private const val BOOK = ":book"
    private const val EDU_SUBSCRIPTIONS = ":edu_subscription"

    // Travel
    private const val TRA_ACCOMMODATION = ":tra_accommodation"
    private const val TRA_TRANSPORTATION = ":tra_transportation"
    private const val TRA_FOOD = ":tra_food"
    private const val TRA_ACTIVITY = ":tra_activity"

    // Gifts & Donation
    private const val GIFT = ":gift"
    private const val WEDDINGS = ":weddings"
    private const val CONDOLATORY = ":condolatory"
    private const val DONATION = ":donation"

    // Pet
    private const val PET_FOOD = ":pet_food"
    private const val PET_SNACK = ":pet_snack"
    private const val PET_TOYS_CLOTHES = ":pet_toys_clothes"
    private const val PET_VET_VISIT = ":pet_vet_visit"

    // Kids
    private const val KID_SCHOOL_FEE = ":kid_school_fee"
    private const val KID_SCHOOL_SUPPLIES = ":kid_school_supplies"
    private const val KID_TOYS = ":kid_toys"
    private const val KID_ALLOWANCES = ":kid_allowances"
    private const val KID_DIAPERS = ":kid_diapers"

    // Insurance
    private const val LIFE_INSURANCE = ":life_insurance"
    private const val MEDICAL_INSURANCE = ":medical_insurance"
    private const val FIRE_INSURANCE = ":fire_insurance"
    private const val SAVINGS_INSURANCE = ":savings_insurance"

    val patternColors = arrayOf(
        R.color.colorNormalExpense,
        R.color.colorWasteExpense,
        R.color.colorInvestExpense
    )

    val patternChipBackgroundColorStateList = arrayOf(
        R.color.chip_normal_background_state_list,
        R.color.chip_waste_background_state_list,
        R.color.chip_invest_background_state_list
    )

    val patternChipStrokeColorStateList = arrayOf(
        R.color.chip_normal_stroke_color_state_list,
        R.color.chip_waste_stroke_color_state_list,
        R.color.chip_invest_stroke_color_state_list
    )

    enum class TYPE {
        EXPENSE,
        INCOME
    }

    enum class PAYMENT {
        CREDIT,
        CASH,
        INCOME
    }

    enum class PATTERN {
        NORMAL,
        WASTE,
        INVEST,
        INCOME
    }

    enum class Status {
        LOADING,
        SUCCESS,
        ERROR
    }

    enum class SortType {
        AMOUNT,
        DATE,
        CATEGORY
    }

    enum class AppTheme(
        val modeNight: Int,
        @StringRes val modeNameRes: Int
    ) {
        FOLLOW_SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, R.string.follow_system),
        LIGHT(AppCompatDelegate.MODE_NIGHT_NO, R.string.light_theme),
        DARK(AppCompatDelegate.MODE_NIGHT_YES, R.string.dark_theme)
    }

    fun incomeCategories(): List<Category> {
        return listOf(
            Category(SALARY, R.string.category_salary, R.drawable.category_food_drink, R.color.category_salary),
            Category(INTEREST_DIVIDEND, R.string.category_interest_dividend, R.drawable.category_food_drink, R.color.category_interest_dividend),
            Category(BONUS, R.string.category_bonus, R.drawable.category_food_drink, R.color.category_bonus),
            Category(SALE, R.string.category_sale, R.drawable.category_food_drink, R.color.category_sale),
            Category(REFUND, R.string.category_refund, R.drawable.category_food_drink, R.color.category_refund),
            Category(OTHER_INCOME, R.string.category_other_income, R.drawable.category_food_drink, R.color.category_other_income),
        )
    }


    fun expenseCategories(): List<Category> {
        return listOf(
            Category(FOOD_DRINKS, R.string.category_food_drink, R.drawable.category_food_drink, R.color.category_food_drink),
            Category(SHOPPING, R.string.category_shopping, R.drawable.category_shopping, R.color.category_shopping),
            Category(PERSONAL_CARE, R.string.category_personal_care, R.drawable.category_personal_care, R.color.category_personal_care),
            Category(TRANSPORTATION, R.string.category_transportation, R.drawable.category_transportation, R.color.category_transportation),
            Category(COMMUNICATION, R.string.category_communication, R.drawable.category_communication, R.color.category_communication),
            Category(ENTERTAINMENT, R.string.category_entertainment, R.drawable.category_entertainment, R.color.category_entertainment),
            Category(HOUSEHOLD, R.string.category_household, R.drawable.category_household, R.color.category_household),
            Category(UTILITY, R.string.category_utility, R.drawable.category_utility, R.color.category_utility),
            Category(HEALTH_CARE, R.string.category_healthcare, R.drawable.category_healthcare, R.color.category_healthcare),
            Category(EDUCATION, R.string.category_education, R.drawable.category_education, R.color.category_education),
            Category(TRAVEL, R.string.category_travel, R.drawable.category_travel, R.color.category_travel),
            Category(GIFTS_DONATION, R.string.category_gifts_donation, R.drawable.category_gifts_donation, R.color.category_gifts_donation),
            Category(PET, R.string.category_pet, R.drawable.category_pet, R.color.category_pet),
            Category(KIDS, R.string.category_kids, R.drawable.category_kids, R.color.category_kids),
            Category(INSURANCE, R.string.category_insurance, R.drawable.category_insurance, R.color.category_insurance),
            Category(TAXES, R.string.category_tax, R.drawable.category_tax, R.color.category_tax),
            Category(OTHER_EXPENSE, R.string.category_other_expense, R.drawable.category_other_expense, R.color.category_other_expense)
        )
    }

    fun expenseSubCategories(): List<SubCategory> {
        return listOf(
            SubCategory(EATING_OUT, R.string.sub_category_eatingout, R.drawable.category_food_drink, R.color.sub_category_breakfast, FOOD_DRINKS),
            SubCategory(GROCERY, R.string.sub_category_grocery, R.drawable.category_food_drink, R.color.sub_category_grocery, FOOD_DRINKS),
            SubCategory(SNACK, R.string.sub_category_snack, R.drawable.category_food_drink, R.color.sub_category_snack, FOOD_DRINKS),
            SubCategory(COFFEE, R.string.sub_category_coffee, R.drawable.category_food_drink, R.color.sub_category_coffee_beverage, FOOD_DRINKS),
            SubCategory(BEVERAGE, R.string.sub_category_beverage, R.drawable.category_food_drink, R.color.sub_category_lunch, FOOD_DRINKS),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_food_drink, R.color.sub_category_other, FOOD_DRINKS),

            SubCategory(CLOTHING, R.string.sub_category_clothing, R.drawable.category_clothing_beauty, R.color.category_shopping, SHOPPING),
            SubCategory(SHOES, R.string.sub_category_shoes, R.drawable.category_clothing_beauty, R.color.category_shopping, SHOPPING),
            SubCategory(ACCESSORY, R.string.sub_category_accessory, R.drawable.category_clothing_beauty, R.color.category_shopping, SHOPPING),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_food_drink, R.color.sub_category_other, SHOPPING),

            SubCategory(HAIRCUT, R.string.sub_category_haircut, R.drawable.category_clothing_beauty, R.color.category_shopping, PERSONAL_CARE),
            SubCategory(BEAUTY, R.string.sub_category_beauty, R.drawable.category_clothing_beauty, R.color.category_shopping, PERSONAL_CARE),
            SubCategory(COSMETICS, R.string.sub_category_beauty, R.drawable.category_clothing_beauty, R.color.category_shopping, PERSONAL_CARE),
            SubCategory(LAUNDRY_MENDING, R.string.sub_category_laundry_mending, R.drawable.category_clothing_beauty, R.color.category_shopping, PERSONAL_CARE),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_clothing_beauty, R.color.category_shopping, PERSONAL_CARE),

            SubCategory(METRO, R.string.sub_category_bus, R.drawable.category_transportation, R.color.category_transportation, TRANSPORTATION),
            SubCategory(BUS, R.string.sub_category_bus, R.drawable.category_transportation, R.color.category_transportation, TRANSPORTATION),
            SubCategory(TAXI, R.string.sub_category_taxi, R.drawable.category_transportation, R.color.category_transportation, TRANSPORTATION),
            SubCategory(CAR_GASFUEL, R.string.sub_category_metro, R.drawable.category_transportation, R.color.category_transportation, TRANSPORTATION),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_transportation, R.color.category_transportation, TRANSPORTATION),

            SubCategory(MOBILE, R.string.sub_category_mobile, R.drawable.category_communication, R.color.category_communication, COMMUNICATION),
            SubCategory(INTERNET_TV, R.string.sub_category_internet_tv, R.drawable.category_communication, R.color.category_communication, COMMUNICATION),
            SubCategory(COM_SUBSCRIPTIONS, R.string.sub_category_internet_tv, R.drawable.category_communication, R.color.category_communication, COMMUNICATION),
            SubCategory(OTHER, R.string.sub_category_internet_tv, R.drawable.category_communication, R.color.category_communication, COMMUNICATION),

            SubCategory(MOVIE, R.string.sub_category_movie, R.drawable.category_entertainment, R.color.category_entertainment, ENTERTAINMENT),
            SubCategory(PERFORMANCE, R.string.sub_category_performance, R.drawable.category_entertainment, R.color.category_entertainment, ENTERTAINMENT),
            SubCategory(GAME, R.string.sub_category_game, R.drawable.category_entertainment, R.color.category_entertainment, ENTERTAINMENT),
            SubCategory(HOBBIES, R.string.sub_category_game, R.drawable.category_entertainment, R.color.category_entertainment, ENTERTAINMENT),
            SubCategory(OTHER, R.string.sub_category_game, R.drawable.category_entertainment, R.color.category_entertainment, ENTERTAINMENT),

            SubCategory(HOME_APPLIANCES, R.string.sub_category_home_appliances, R.drawable.category_household, R.color.category_household, HOUSEHOLD),
            SubCategory(FURNITURE_DECOR, R.string.sub_category_furniture_decor, R.drawable.category_household, R.color.category_household, HOUSEHOLD),
            SubCategory(KITCHEN, R.string.sub_category_kitchen, R.drawable.category_household, R.color.category_household, HOUSEHOLD),
            SubCategory(CLEANING_SUPPLIES, R.string.sub_category_cleaning_supplies, R.drawable.category_household, R.color.category_household, HOUSEHOLD),
            SubCategory(LAUNDRY_MENDING, R.string.sub_category_laundry_mending, R.drawable.category_household, R.color.category_household, HOUSEHOLD),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_household, R.color.category_household, HOUSEHOLD),

            SubCategory(HOUSE_RENT, R.string.sub_category_house_rent, R.drawable.category_utility, R.color.category_utility, UTILITY),
            SubCategory(MAINTENANCE_FEE, R.string.sub_category_maintenance_fee, R.drawable.category_utility, R.color.category_utility, UTILITY),
            SubCategory(ELECTRIC, R.string.sub_category_electric, R.drawable.category_utility, R.color.category_utility, UTILITY),
            SubCategory(WATER, R.string.sub_category_water, R.drawable.category_utility, R.color.category_utility, UTILITY),
            SubCategory(UTILITY_GAS, R.string.sub_category_utility_gas, R.drawable.category_utility, R.color.category_utility, UTILITY),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_utility, R.color.category_utility, UTILITY),

            SubCategory(HOSPITAL, R.string.sub_category_hospital, R.drawable.category_healthcare, R.color.category_healthcare, HEALTH_CARE),
            SubCategory(MEDICINE, R.string.sub_category_medicine, R.drawable.category_healthcare, R.color.category_healthcare, HEALTH_CARE),
            SubCategory(VITAMIN, R.string.sub_category_vitamin, R.drawable.category_healthcare, R.color.category_healthcare, HEALTH_CARE),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_healthcare, R.color.category_healthcare, HEALTH_CARE),

            SubCategory(TUITION, R.string.sub_category_tuition, R.drawable.category_education, R.color.category_education, EDUCATION),
            SubCategory(BOOK, R.string.sub_category_book, R.drawable.category_education, R.color.category_education, EDUCATION),
            SubCategory(EDU_SUBSCRIPTIONS, R.string.sub_category_edu_subscriptions, R.drawable.category_education, R.color.category_education, EDUCATION),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_education, R.color.category_education, EDUCATION),

            SubCategory(TRA_ACCOMMODATION, R.string.sub_category_tra_accommodation, R.drawable.category_travel, R.color.category_travel, TRAVEL),
            SubCategory(TRA_TRANSPORTATION, R.string.sub_category_tra_transportation, R.drawable.category_travel, R.color.category_travel, TRAVEL),
            SubCategory(TRA_FOOD, R.string.sub_category_tra_food, R.drawable.category_travel, R.color.category_travel, TRAVEL),
            SubCategory(TRA_ACTIVITY, R.string.sub_category_tra_activity, R.drawable.category_travel, R.color.category_travel, TRAVEL),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_travel, R.color.category_travel, TRAVEL),

            SubCategory(GIFT, R.string.sub_category_gift, R.drawable.category_gifts_donation, R.color.category_gifts_donation, GIFTS_DONATION),
            SubCategory(WEDDINGS, R.string.sub_category_weddings, R.drawable.category_gifts_donation, R.color.category_gifts_donation, GIFTS_DONATION),
            SubCategory(CONDOLATORY, R.string.sub_category_condolatory, R.drawable.category_gifts_donation, R.color.category_gifts_donation, GIFTS_DONATION),
            SubCategory(DONATION, R.string.sub_category_donation, R.drawable.category_gifts_donation, R.color.category_gifts_donation, GIFTS_DONATION),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_gifts_donation, R.color.category_gifts_donation, GIFTS_DONATION),

            SubCategory(PET_FOOD, R.string.sub_category_pet_food, R.drawable.category_pet, R.color.category_gifts_donation, PET),
            SubCategory(PET_SNACK, R.string.sub_category_pet_snack, R.drawable.category_pet, R.color.category_gifts_donation, PET),
            SubCategory(PET_TOYS_CLOTHES, R.string.sub_category_pet_toys_clothing, R.drawable.category_pet, R.color.category_gifts_donation, PET),
            SubCategory(PET_VET_VISIT, R.string.sub_category_pet_vet_visit, R.drawable.category_pet, R.color.category_gifts_donation, PET),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_pet, R.color.category_gifts_donation, PET),

            SubCategory(KID_SCHOOL_FEE, R.string.sub_category_kid_school_fee, R.drawable.category_gifts_donation, R.color.category_gifts_donation, KIDS),
            SubCategory(KID_SCHOOL_SUPPLIES, R.string.sub_category_kid_school_supplies, R.drawable.category_gifts_donation, R.color.category_gifts_donation, KIDS),
            SubCategory(KID_TOYS, R.string.sub_category_kid_school_toys, R.drawable.category_gifts_donation, R.color.category_gifts_donation, KIDS),
            SubCategory(KID_ALLOWANCES, R.string.sub_category_kid_school_allowances, R.drawable.category_gifts_donation, R.color.category_gifts_donation, KIDS),
            SubCategory(KID_DIAPERS, R.string.sub_category_kid_school_diapers, R.drawable.category_gifts_donation, R.color.category_gifts_donation, KIDS),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_gifts_donation, R.color.category_gifts_donation, KIDS),

            SubCategory(LIFE_INSURANCE, R.string.sub_category_life_insurance, R.drawable.category_gifts_donation, R.color.category_gifts_donation, INSURANCE),
            SubCategory(MEDICAL_INSURANCE, R.string.sub_category_medical_insurance, R.drawable.category_gifts_donation, R.color.category_gifts_donation, INSURANCE),
            SubCategory(FIRE_INSURANCE, R.string.sub_category_fire_insurance, R.drawable.category_gifts_donation, R.color.category_gifts_donation, INSURANCE),
            SubCategory(SAVINGS_INSURANCE, R.string.sub_category_savings_insurance, R.drawable.category_gifts_donation, R.color.category_gifts_donation, INSURANCE),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_gifts_donation, R.color.category_gifts_donation, INSURANCE),

            SubCategory(TAXES, R.string.category_tax, R.drawable.category_tax, R.color.category_gifts_donation, TAXES),
            SubCategory(OTHER_EXPENSE, R.string.category_other_expense, R.drawable.category_other_expense, R.color.category_gifts_donation, OTHER_EXPENSE),
            )
    }
}