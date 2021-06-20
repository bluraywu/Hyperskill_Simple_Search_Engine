package search

import java.io.File

fun main(args: Array<String>) {
    val file = File(args[1])
    val invertedIndex = mutableMapOf<String, MutableList<Int>>()

    val peopleList = mutableListOf<String>()
    file.forEachLine {
        peopleList.add(it)
    }
    for ((index, it) in peopleList.withIndex()) {
        val splitStr = it.split(" ")
        splitStr.forEach {
            val value = it.toLowerCase()
            if (invertedIndex.containsKey(value)) {
                invertedIndex[value]?.add(index)
            } else {
                invertedIndex[value] = mutableListOf(index)
            }
        }
    }
    //println(invertedIndex)
    while (true) {
        println(
            """
        === Menu ===
        1. Find a person
        2. Print all people
        0. Exit
    """.trimIndent()
        )
        when (readLine()!!.toString()) {
            "1" -> {
                println("Select a matching strategy: ALL, ANY, NONE")
                val strategy = readLine()!!.toString()
                println("Enter a name or email to search all suitable people.")
                val keywords = readLine()!!.split(" ").map(String::toLowerCase)
                searchPeople(keywords, peopleList, invertedIndex, STRATEGY.valueOf(strategy))
            }
            "2" -> {
                println("=== List of people ===")
                peopleList.forEach(::println)
            }
            "0" -> {
                println("Bye!")
                break
            }
            else -> {
                println("Incorrect option! Try again.")
            }
        }
    }
}

enum class STRATEGY {
    ALL,
    ANY,
    NONE
}

fun searchPeople(
    keywords: List<String>,
    peopleList: List<String>,
    invertedIndex: Map<String, MutableList<Int>>,
    strategy: STRATEGY
) {
    var results = searchIndex(keywords[0], invertedIndex)
    when (strategy) {
        STRATEGY.ALL -> {
            for (i in 1..keywords.lastIndex) {
                val rsSet = searchIndex(keywords[i], invertedIndex)
                results = results.intersect(rsSet)
            }
        }
        STRATEGY.ANY -> {
            for (i in 1..keywords.lastIndex) {
                val rsSet = searchIndex(keywords[i], invertedIndex)
                results = results.union(rsSet)
            }
        }
        STRATEGY.NONE -> {
            results = (0..peopleList.lastIndex).toSet()
            keywords.forEach {
                val rsSet = searchIndex(it, invertedIndex)
                results = results.minus(rsSet)
            }
        }
    }
    if (results.isNotEmpty()) {
        results.forEach { println(peopleList[it]) }
    } else {
        println("No matching people found.")
    }
}

fun searchIndex(
    keyword: String,
    invertedIndex: Map<String, MutableList<Int>>
): Set<Int> = invertedIndex[keyword.toLowerCase()]?.toSet() ?: emptySet()
