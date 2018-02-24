/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.example.text.styling.parser

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for [Parser]
 */
class ParserTest {

    private val separator = System.getProperty("line.separator")

    // PROBE to verify that one test fails in the suite
    @Test
    fun test_fails() {
        assertEquals(0, 2)
    }
    
    @Test fun quoteBeginningOfText() {
        val withQuote = "> This is a quote.$separator"+"This is not"

        val elements = Parser.parse(withQuote).elements

        val expected = listOf(
                Element(Element.Type.QUOTE, "This is a quote.$separator"),
                Element(Element.Type.TEXT, "This is not"))
        assertEquals(elements, expected)
    }

    @Test fun quoteEndOfText() {
        val withQuote = "This is not a quote.$separator> This is a quote"

        val elements = Parser.parse(withQuote).elements

        val expected = listOf(
                Element(Element.Type.TEXT, "This is not a quote.$separator"),
                Element(Element.Type.QUOTE, "This is a quote"))
        assertEquals(elements, expected)
    }

    @Test fun simpleBulletPoints() {
        val bulletPoints = "Bullet points:$separator* One$separator+ Two$separator* Three"

        val elements = Parser.parse(bulletPoints).elements

        assertEquals(elements.size, 4)
        assertEquals(elements[0].type, Element.Type.TEXT)
        assertEquals(elements[0].text, "Bullet points:$separator")
        assertEquals(elements[1].type, Element.Type.BULLET_POINT)
        assertEquals(elements[1].text, "One$separator")
        assertEquals(elements[2].type, Element.Type.BULLET_POINT)
        assertEquals(elements[2].text, "Two$separator")
        assertEquals(elements[3].type, Element.Type.BULLET_POINT)
        assertEquals(elements[3].text, "Three")
    }

    @Test fun simpleCode() {
        val code = "Styling `Text` in `Kotlin`"

        val elements = Parser.parse(code).elements

        val expected = listOf(
                Element(Element.Type.TEXT, "Styling "),
                Element(Element.Type.CODE_BLOCK, "Text"),
                Element(Element.Type.TEXT, " in "),
                Element(Element.Type.CODE_BLOCK, "Kotlin"))
        assertEquals(elements, expected)
    }

    @Test fun codeWithExtraTick() {
        val code = "Styling `Text` in `Kotlin"

        val elements = Parser.parse(code).elements

        val expected = listOf(
                Element(Element.Type.TEXT, "Styling "),
                Element(Element.Type.CODE_BLOCK, "Text"),
                Element(Element.Type.TEXT, " in "),
                Element(Element.Type.TEXT, "`Kotlin"))
        assertEquals(elements, expected)
    }

    @Test fun quoteBulletPointsCode() {
        val text =  """Complex:$separator> Quote${separator}With points:$separator+ bullet `one`$separator* bullet `two` is `long`"""

        val elements = Parser.parse(text).elements

        assertEquals(elements.size, 5)
        assertEquals(elements[0].type, Element.Type.TEXT)
        assertEquals(elements[0].text, "Complex:$separator")
        assertEquals(elements[1].type, Element.Type.QUOTE)
        assertEquals(elements[1].text, "Quote$separator")
        assertEquals(elements[2].type, Element.Type.TEXT)
        assertEquals(elements[2].text, "With points:$separator")
        // first bullet point
        assertEquals(elements[3].type, Element.Type.BULLET_POINT)
        assertEquals(elements[3].text, "bullet `one`$separator")
        val subElements1 = elements[3].elements
        assertEquals(subElements1.size, 3)
        assertEquals(subElements1[0].type, Element.Type.TEXT)
        assertEquals(subElements1[0].text, "bullet ")
        assertEquals(subElements1[1].type, Element.Type.CODE_BLOCK)
        assertEquals(subElements1[1].text, "one")
        assertEquals(subElements1[2].type, Element.Type.TEXT)
        assertEquals(subElements1[2].text, "$separator")
        // second bullet point
        assertEquals(elements[4].type, Element.Type.BULLET_POINT)
        assertEquals(elements[4].text, "bullet `two` is `long`")
        val subElements2 = elements[4].elements
        assertEquals(subElements2.size, 4)
        assertEquals(subElements2[0].type, Element.Type.TEXT)
        assertEquals(subElements2[0].text, "bullet ")
        assertEquals(subElements2[1].type, Element.Type.CODE_BLOCK)
        assertEquals(subElements2[1].text, "two")
        assertEquals(subElements2[2].type, Element.Type.TEXT)
        assertEquals(subElements2[2].text, " is ")
        assertEquals(subElements2[3].type, Element.Type.CODE_BLOCK)
        assertEquals(subElements2[3].text, "long")
    }
}