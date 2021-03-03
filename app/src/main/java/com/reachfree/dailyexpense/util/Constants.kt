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
    private const val INTEREST_DIVIDEND = ":interest&dividend"
    private const val BONUS = ":bonus"
    private const val SALE = ":sale"
    private const val REFUND = ":refund"
    private const val OTHER_INCOME = ":other_income"

    const val FOOD_DRINK = ":food&drink"
    private const val UTILITY = ":utility"
    private const val COMMUNICATION = ":communication"
    private const val HOUSEHOLD = ":household"
    private const val CLOTHING_BEAUTY = ":clothing&beauty"
    private const val EDUCATION = ":education"
    private const val ENTERTAINMENT = ":entertainment"
    private const val HEALTHCARE = ":healthcare"
    private const val GIFTS_DONATION = ":gifts&donation"
    private const val TRANSPORTATION = ":transportation"
    private const val TRAVEL = ":travel"
    private const val TAXES = ":taxes"
    private const val OTHER_EXPENSE = ":other_expense"

    const val BREAKFAST = ":breakfast"
    private const val LUNCH = ":lunch"
    private const val DINNER = ":dinner"
    private const val GROCERY = ":grocery"
    private const val SNACK = ":snack"
    private const val COFFEE_BEVERAGE = ":coffee&beverage"
    private const val OTHER = ":other"
    private const val MAINTENANCE_CHARGE = ":maintenance_charge"
    private const val ELECTRICITY_BILL = ":electricity_bill"
    private const val WATER_BILL = ":water_bill"
    private const val GAS_BILL = ":gas_bill"
    private const val MOBILE = ":mobile"
    private const val INTERNET_TV = ":internet&tv"
    private const val FURNITURE_APPLIANCES = ":furniture&appliances"
    private const val KITCHEN_PRODUCTS = ":kitchen_products"
    private const val WASHROOM_PRODUCTS = ":washroom_products"
    private const val MISCELLANEOUS = ":miscellaneous"
    private const val CLOTHING = ":clothing"
    private const val ACCESSORY = ":accessory"
    private const val SHOES = ":shoes"
    private const val HAIR = ":hair"
    private const val BEAUTY = ":beauty"
    private const val LAUNDRY = ":laundry"
    private const val ACADEMY = ":academy"
    private const val BOOK = ":book"
    private const val LECTURE = ":lecture"
    private const val MOVIE = ":movie"
    private const val PERFORMANCE = ":performance"
    private const val GAME = ":game"
    private const val HOSPITAL = ":hospital"
    private const val DRUG = ":drug"
    private const val SPORTS = ":sports"
    private const val GIFT = ":gift"
    private const val MARRIAGE = ":marriage"
    private const val FUNERAL = ":funeral"
    private const val DONATION = ":donation"
    private const val BUS = ":bus"
    private const val TAXI = ":taxi"
    private const val METRO = ":metro"
    private const val INBOUND = ":inbound"
    private const val OUTBOUND = ":outbound"

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
            Category(FOOD_DRINK, R.string.category_food_drink, R.drawable.category_food_drink, R.color.category_food_drink),
            Category(UTILITY, R.string.category_utility, R.drawable.category_utility, R.color.category_utility),
            Category(COMMUNICATION, R.string.category_communication, R.drawable.category_communication, R.color.category_communication),
            Category(HOUSEHOLD, R.string.category_household, R.drawable.category_household, R.color.category_household),
            Category(CLOTHING_BEAUTY, R.string.category_clothing_beauty, R.drawable.category_clothing_beauty, R.color.category_clothing_beauty),
            Category(EDUCATION, R.string.category_education, R.drawable.category_education, R.color.category_education),
            Category(ENTERTAINMENT, R.string.category_entertainment, R.drawable.category_entertainment, R.color.category_entertainment),
            Category(HEALTHCARE, R.string.category_healthcare, R.drawable.category_healthcare, R.color.category_healthcare),
            Category(GIFTS_DONATION, R.string.category_gifts_donation, R.drawable.category_gifts_donation, R.color.category_gifts_donation),
            Category(TRANSPORTATION, R.string.category_transportation, R.drawable.category_transportation, R.color.category_transportation),
            Category(TRAVEL, R.string.category_travel, R.drawable.category_travel, R.color.category_travel),
            Category(TAXES, R.string.category_tax, R.drawable.category_tax, R.color.category_tax),
            Category(OTHER_EXPENSE, R.string.category_other_expense, R.drawable.category_other_expense, R.color.category_other_expense)
        )
    }

    fun expenseSubCategories(): List<SubCategory> {
        return listOf(
            SubCategory(BREAKFAST, R.string.sub_category_breakfast, R.drawable.category_food_drink, R.color.sub_category_breakfast, FOOD_DRINK),
            SubCategory(LUNCH, R.string.sub_category_lunch, R.drawable.category_food_drink, R.color.sub_category_lunch, FOOD_DRINK),
            SubCategory(DINNER, R.string.sub_category_dinner, R.drawable.category_food_drink, R.color.sub_category_dinner, FOOD_DRINK),
            SubCategory(GROCERY, R.string.sub_category_grocery, R.drawable.category_food_drink, R.color.sub_category_grocery, FOOD_DRINK),
            SubCategory(SNACK, R.string.sub_category_snack, R.drawable.category_food_drink, R.color.sub_category_snack, FOOD_DRINK),
            SubCategory(COFFEE_BEVERAGE, R.string.sub_category_coffee_beverage, R.drawable.category_food_drink, R.color.sub_category_coffee_beverage, FOOD_DRINK),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_food_drink, R.color.sub_category_other, FOOD_DRINK),

            SubCategory(MAINTENANCE_CHARGE, R.string.sub_category_maintenance_charge, R.drawable.category_utility, R
                .color.category_utility, UTILITY),
            SubCategory(ELECTRICITY_BILL, R.string.sub_category_electricity_bill, R.drawable.category_utility, R.color.category_utility, UTILITY),
            SubCategory(WATER_BILL, R.string.sub_category_water_bill, R.drawable.category_utility, R.color.category_utility, UTILITY),
            SubCategory(GAS_BILL, R.string.sub_category_gas_bill, R.drawable.category_utility, R.color.category_utility, UTILITY),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_utility, R.color.category_utility, UTILITY),

            SubCategory(MOBILE, R.string.sub_category_mobile, R.drawable.category_communication, R.color.category_communication, COMMUNICATION),
            SubCategory(INTERNET_TV, R.string.sub_category_internet_tv, R.drawable.category_communication, R.color.category_communication, COMMUNICATION),
            SubCategory(OTHER, R.string.sub_category_internet_tv, R.drawable.category_communication, R.color.category_communication, COMMUNICATION),

            SubCategory(FURNITURE_APPLIANCES, R.string.sub_category_furniture_appliances, R.drawable.category_household, R.color.category_household, HOUSEHOLD),
            SubCategory(KITCHEN_PRODUCTS, R.string.sub_category_kitchen_products, R.drawable.category_household, R.color.category_household, HOUSEHOLD),
            SubCategory(WASHROOM_PRODUCTS, R.string.sub_category_washroom_products, R.drawable.category_household, R.color.category_household, HOUSEHOLD),
            SubCategory(MISCELLANEOUS, R.string.sub_category_miscellaneous, R.drawable.category_household, R.color.category_household, HOUSEHOLD),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_household, R.color.category_household, HOUSEHOLD),

            SubCategory(CLOTHING, R.string.sub_category_clothing, R.drawable.category_clothing_beauty, R.color.category_clothing_beauty, CLOTHING_BEAUTY),
            SubCategory(ACCESSORY, R.string.sub_category_accessory, R.drawable.category_clothing_beauty, R.color.category_clothing_beauty, CLOTHING_BEAUTY),
            SubCategory(SHOES, R.string.sub_category_shoes, R.drawable.category_clothing_beauty, R.color.category_clothing_beauty, CLOTHING_BEAUTY),
            SubCategory(HAIR, R.string.sub_category_hair, R.drawable.category_clothing_beauty, R.color.category_clothing_beauty, CLOTHING_BEAUTY),
            SubCategory(BEAUTY, R.string.sub_category_beauty, R.drawable.category_clothing_beauty, R.color.category_clothing_beauty, CLOTHING_BEAUTY),
            SubCategory(LAUNDRY, R.string.sub_category_laundry, R.drawable.category_clothing_beauty, R.color.category_clothing_beauty, CLOTHING_BEAUTY),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_clothing_beauty, R.color.category_clothing_beauty, CLOTHING_BEAUTY),

            SubCategory(ACADEMY, R.string.sub_category_academy, R.drawable.category_education, R.color.category_education, EDUCATION),
            SubCategory(BOOK, R.string.sub_category_book, R.drawable.category_education, R.color.category_education, EDUCATION),
            SubCategory(LECTURE, R.string.sub_category_lecture, R.drawable.category_education, R.color.category_education, EDUCATION),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_education, R.color.category_education, EDUCATION),

            SubCategory(MOVIE, R.string.sub_category_movie, R.drawable.category_entertainment, R.color.category_entertainment, ENTERTAINMENT),
            SubCategory(PERFORMANCE, R.string.sub_category_performance, R.drawable.category_entertainment, R.color.category_entertainment, ENTERTAINMENT),
            SubCategory(GAME, R.string.sub_category_game, R.drawable.category_entertainment, R.color.category_entertainment, ENTERTAINMENT),
            SubCategory(OTHER, R.string.sub_category_game, R.drawable.category_entertainment, R.color.category_entertainment, ENTERTAINMENT),

            SubCategory(HOSPITAL, R.string.sub_category_hospital, R.drawable.category_healthcare, R.color.category_healthcare, HEALTHCARE),
            SubCategory(DRUG, R.string.sub_category_drug, R.drawable.category_healthcare, R.color.category_healthcare, HEALTHCARE),
            SubCategory(SPORTS, R.string.sub_category_sports, R.drawable.category_healthcare, R.color.category_healthcare, HEALTHCARE),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_healthcare, R.color.category_healthcare, HEALTHCARE),

            SubCategory(GIFT, R.string.sub_category_gift, R.drawable.category_gifts_donation, R.color.category_gifts_donation, GIFTS_DONATION),
            SubCategory(MARRIAGE, R.string.sub_category_marriage, R.drawable.category_gifts_donation, R.color.category_gifts_donation, GIFTS_DONATION),
            SubCategory(FUNERAL, R.string.sub_category_funeral, R.drawable.category_gifts_donation, R.color.category_gifts_donation, GIFTS_DONATION),
            SubCategory(DONATION, R.string.sub_category_donation, R.drawable.category_gifts_donation, R.color.category_gifts_donation, GIFTS_DONATION),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_gifts_donation, R.color.category_gifts_donation, GIFTS_DONATION),

            SubCategory(BUS, R.string.sub_category_bus, R.drawable.category_transportation, R.color.category_transportation, TRANSPORTATION),
            SubCategory(TAXI, R.string.sub_category_taxi, R.drawable.category_transportation, R.color.category_transportation, TRANSPORTATION),
            SubCategory(METRO, R.string.sub_category_metro, R.drawable.category_transportation, R.color.category_transportation, TRANSPORTATION),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_transportation, R.color.category_transportation, TRANSPORTATION),

            SubCategory(INBOUND, R.string.sub_category_inbound, R.drawable.category_travel, R.color.category_travel, TRAVEL),
            SubCategory(OUTBOUND, R.string.sub_category_outbound, R.drawable.category_travel, R.color.category_travel, TRAVEL),
            SubCategory(OTHER, R.string.sub_category_other, R.drawable.category_travel, R.color.category_travel, TRAVEL),
        )
    }
}