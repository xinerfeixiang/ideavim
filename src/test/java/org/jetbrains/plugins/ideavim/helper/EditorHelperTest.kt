/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.helper

import com.maddyhome.idea.vim.helper.EditorHelper
import org.jetbrains.plugins.ideavim.SkipNeovimReason
import org.jetbrains.plugins.ideavim.TestWithoutNeovim
import org.jetbrains.plugins.ideavim.VimTestCase
import org.junit.Assert
import kotlin.math.roundToInt

class EditorHelperTest : VimTestCase() {
  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test scroll column to left of screen`() {
    configureByColumns(100)
    EditorHelper.scrollColumnToLeftOfScreen(myFixture.editor, 0, 2)
    val visibleArea = myFixture.editor.scrollingModel.visibleArea
    val columnWidth = EditorHelper.getPlainSpaceWidthFloat(myFixture.editor)
    Assert.assertEquals((2 * columnWidth).roundToInt(), visibleArea.x)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test scroll column to right of screen`() {
    configureByColumns(100)
    val column = screenWidth + 2
    EditorHelper.scrollColumnToRightOfScreen(myFixture.editor, 0, column)
    val visibleArea = myFixture.editor.scrollingModel.visibleArea
    val columnWidth = EditorHelper.getPlainSpaceWidthFloat(myFixture.editor)
    Assert.assertEquals(((column - screenWidth + 1) * columnWidth).roundToInt(), visibleArea.x)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test scroll column to middle of screen with even number of columns`() {
    configureByColumns(200)
    // For an 80 column screen, moving a column to the centre should position it in column 41 (1 based) - 40 columns on
    // the left, mid point, 39 columns on the right
    // Put column 100 into position 41 -> offset is 59 columns
    EditorHelper.scrollColumnToMiddleOfScreen(myFixture.editor, 0, 99)
    val visibleArea = myFixture.editor.scrollingModel.visibleArea
    val columnWidth = EditorHelper.getPlainSpaceWidthFloat(myFixture.editor)
    Assert.assertEquals((59 * columnWidth).roundToInt(), visibleArea.x)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test scroll column to middle of screen with odd number of columns`() {
    configureByColumns(200)
    setEditorVisibleSize(81, 25)
    // For an 81 column screen, moving a column to the centre should position it in column 41 (1 based) - 40 columns on
    // the left, mid point, 40 columns on the right
    // Put column 100 into position 41 -> offset is 59 columns
    EditorHelper.scrollColumnToMiddleOfScreen(myFixture.editor, 0, 99)
    val visibleArea = myFixture.editor.scrollingModel.visibleArea
    val columnWidth = EditorHelper.getPlainSpaceWidthFloat(myFixture.editor)
    Assert.assertEquals((59 * columnWidth).roundToInt(), visibleArea.x)
  }
}
