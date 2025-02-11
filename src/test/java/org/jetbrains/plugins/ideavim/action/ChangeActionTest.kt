/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */
package org.jetbrains.plugins.ideavim.action

import com.intellij.codeInsight.folding.CodeFoldingManager
import com.intellij.codeInsight.folding.impl.FoldingUtil
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.command.VimStateMachine
import com.maddyhome.idea.vim.helper.VimBehaviorDiffers
import org.jetbrains.plugins.ideavim.SkipNeovimReason
import org.jetbrains.plugins.ideavim.TestWithoutNeovim
import org.jetbrains.plugins.ideavim.VimTestCase

/**
 * @author vlan
 */
class ChangeActionTest : VimTestCase() {
  // VIM-620 |i_CTRL-O|
  fun testInsertSingleCommandAndInserting() {
    doTest(
      listOf("i", "<C-O>", "a", "123", "<Esc>", "x"), "abc${c}d\n", "abcd12\n", VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  // VIM-620 |i_CTRL-O|
  fun testInsertSingleCommandAndNewLineInserting() {
    doTest(
      listOf("i", "<C-O>", "o", "123", "<Esc>", "x"),
      "abc${c}d\n", "abcd\n12\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-620 |i_CTRL-O|
  fun testInsertSingleCommandAndNewLineInserting2() {
    doTest(
      listOf("i", "<C-O>", "v"),
      "12${c}345", "12${s}${c}3${se}45", VimStateMachine.Mode.INSERT_VISUAL, VimStateMachine.SubMode.VISUAL_CHARACTER
    )
  }

  // VIM-620 |i_CTRL-O|
  fun testInsertSingleCommandAndNewLineInserting3() {
    doTest(
      listOf("i", "<C-O>", "v", "<esc>"),
      "12${c}345", "12${c}345", VimStateMachine.Mode.INSERT, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-620 |i_CTRL-O|
  fun testInsertSingleCommandAndNewLineInserting4() {
    doTest(
      listOf("i", "<C-O>", "v", "d"),
      "12${c}345", "12${c}45", VimStateMachine.Mode.INSERT, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-620 |i_CTRL-O|
  @TestWithoutNeovim(SkipNeovimReason.SELECT_MODE)
  fun testInsertSingleCommandAndNewLineInserting5() {
    doTest(
      listOf("i", "<C-O>", "v", "<C-G>"),
      "12${c}345", "12${s}3${c}${se}45", VimStateMachine.Mode.INSERT_SELECT, VimStateMachine.SubMode.VISUAL_CHARACTER
    )
  }

  // VIM-620 |i_CTRL-O|
  @TestWithoutNeovim(SkipNeovimReason.SELECT_MODE)
  fun testInsertSingleCommandAndNewLineInserting6() {
    doTest(
      listOf("i", "<C-O>", "gh"),
      "12${c}345", "12${s}3${c}${se}45", VimStateMachine.Mode.INSERT_SELECT, VimStateMachine.SubMode.VISUAL_CHARACTER
    )
  }

  // VIM-620 |i_CTRL-O|
  @TestWithoutNeovim(SkipNeovimReason.SELECT_MODE)
  fun testInsertSingleCommandAndNewLineInserting7() {
    doTest(
      listOf("i", "<C-O>", "gh", "<esc>"),
      "12${c}345", "123${c}45", VimStateMachine.Mode.INSERT, VimStateMachine.SubMode.NONE
    )
  }

/*
  // Turn it on after typing via handlers are implemented for tests
  // VIM-620 |i_CTRL-O|
  fun ignoreTestInsertSingleCommandAndNewLineInserting8() {
    doTest(
      listOf("i", "<C-O>", "gh", "d"),
      "12${c}345", "12d${c}45", CommandState.Mode.INSERT, CommandState.SubMode.NONE
    )
  }
*/

  // VIM-311 |i_CTRL-O|
  fun testInsertSingleCommand() {
    doTest(
      listOf("i", "def", "<C-O>", "d2h", "x"),
      "abc$c.\n", "abcdx.\n", VimStateMachine.Mode.INSERT, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-321 |d| |count|
  fun testDeleteEmptyRange() {
    doTest("d0", "${c}hello\n", "hello\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
  }

  // VIM-157 |~|
  fun testToggleCharCase() {
    doTest("~~", "${c}hello world\n", "HEllo world\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
  }

  // VIM-157 |~|
  fun testToggleCharCaseLineEnd() {
    doTest("~~", "hello wor${c}ld\n", "hello worLD\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
  }

  fun testToggleCaseMotion() {
    doTest("g~w", "${c}FooBar Baz\n", "fOObAR Baz\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
  }

  fun testChangeUpperCase() {
    doTest(
      "gUw", "${c}FooBar Baz\n", "FOOBAR Baz\n", VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun testChangeLowerCase() {
    doTest("guw", "${c}FooBar Baz\n", "foobar Baz\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
  }

  fun testToggleCaseVisual() {
    doTest(
      "ve~", "${c}FooBar Baz\n", "fOObAR Baz\n", VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun testChangeUpperCaseVisual() {
    doTest(
      "veU", "${c}FooBar Baz\n", "FOOBAR Baz\n", VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun testChangeLowerCaseVisual() {
    doTest(
      "veu", "${c}FooBar Baz\n", "foobar Baz\n", VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  // VIM-85 |i| |gi| |gg|
  fun testInsertAtPreviousAction() {
    doTest(
      listOf("i", "hello", "<Esc>", "gg", "gi", " world! "),
      """
   one
   two ${c}three
   four
   
      """.trimIndent(),
      """
   one
   two hello world! three
   four
   
      """.trimIndent(),
      VimStateMachine.Mode.INSERT, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-312 |d| |w|
  fun testDeleteLastWordInFile() {
    doTest(
      "dw",
      """
        one
        ${c}two
        
      """.trimIndent(),
      """
        one
        
        
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
    assertOffset(4)
  }

  // |d| |w|
  fun testDeleteLastWordBeforeEOL() {
    doTest(
      "dw",
      """
   one ${c}two
   three
   
      """.trimIndent(),
      """
   one 
   three
   
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  // VIM-105 |d| |w|
  fun testDeleteLastWordBeforeEOLs() {
    doTest(
      "dw",
      """
   one ${c}two
   
   three
   
      """.trimIndent(),
      """
   one 
   
   three
   
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-105 |d| |w|
  fun testDeleteLastWordBeforeEOLAndWhitespace() {
    doTest(
      "dw",
      """one ${c}two
 three
""",
      """one 
 three
""",
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
    assertOffset(3)
  }

  // VIM-105 |d| |w| |count|
  fun testDeleteTwoWordsOnTwoLines() {
    doTest(
      "d2w",
      """
   one ${c}two
   three four
   
      """.trimIndent(),
      "one four\n", VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  // VIM-1380 |d| |w| |count|
  fun testDeleteTwoWordsAtLastChar() {
    doTest(
      "d2w", "on${c}e two three\n", "on${c}three\n", VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  // VIM-394 |d| |v_aw|
  fun testDeleteIndentedWordBeforePunctuation() {
    doTest(
      "daw",
      """foo
  ${c}bar, baz
""",
      """foo
  , baz
""",
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  // |d| |v_aw|
  fun testDeleteLastWordAfterPunctuation() {
    doTest(
      "daw",
      """
   foo(${c}bar
   baz
   
      """.trimIndent(),
      """
   foo(
   baz
   
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  // VIM-244 |d| |l|
  fun testDeleteLastCharInLine() {
    doTest(
      "dl",
      """
        fo${c}o
        bar
        
      """.trimIndent(),
      """
        fo
        bar
        
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
    assertOffset(1)
  }

  // VIM-393 |d|
  fun testDeleteBadArgument() {
    doTest(
      listOf("dD", "dd"),
      """
   one
   two
   
      """.trimIndent(),
      "two\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-262 |i_CTRL-R|
  fun testInsertFromRegister() {
    setRegister('a', "World")
    doTest(
      listOf("A", ", ", "<C-R>", "a", "!"), "${c}Hello\n", "Hello, World!\n", VimStateMachine.Mode.INSERT,
      VimStateMachine.SubMode.NONE
    )
  }

  // VIM-404 |O|
  fun testInsertNewLineAboveFirstLine() {
    doTest(
      listOf("O", "bar"),
      "fo${c}o\n", "bar\nfoo\n", VimStateMachine.Mode.INSERT, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-472 |v|
  fun testVisualSelectionRightMargin() {
    doTest(
      listOf("v", "k\$d"),
      "foo\n${c}bar\n", "fooar\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-632 |CTRL-V| |v_d|
  fun testDeleteVisualBlock() {
    doTest(
      listOf("<C-V>", "jjl", "d"),
      """
        ${c}foo
        bar
        baz
        quux
        
      """.trimIndent(),
      """
        ${c}o
        r
        z
        quux
        
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  fun testDeleteCharVisualBlock() {
    doTest(
      listOf("<C-V>", "jjl", "x"),
      """
        ${c}foo
        bar
        baz
        quux
        
      """.trimIndent(),
      """
        ${c}o
        r
        z
        quux
        
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  fun testDeleteJoinLinesSpaces() {
    doTest(
      "3J",
      """    a$c 1
    b 2
    c 3
quux
""",
      """    a 1 b 2 c 3
quux
""",
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  fun testDeleteJoinLines() {
    doTest(
      "3gJ",
      """    a$c 1
    b 2
    c 3
quux
""",
      """    a 1    b 2    c 3
quux
""",
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  @VimBehaviorDiffers(originalVimAfter = "foo  bar")
  fun testDeleteJoinLinesWithTrailingSpaceThenEmptyLine() {
    doTest(
      "3J",
      """
        foo.
        
        bar
      """.dotToSpace().trimIndent(),
      "foo bar", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  fun testDeleteJoinLinesWithTwoTrailingSpaces() {
    doTest(
      "J",
      """
        foo..
        bar
      """.dotToSpace().trimIndent(),
      "foo  bar", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  fun testDeleteJoinVisualLinesSpaces() {
    doTest(
      "v2jJ",
      """    a$c 1
    b 2
    c 3
quux
""",
      """    a 1 b 2 c 3
quux
""",
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  fun testDeleteJoinVisualLines() {
    doTest(
      "v2jgJ",
      """    a$c 1
    b 2
    c 3
quux
""",
      """    a 1    b 2    c 3
quux
""",
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  fun testDeleteCharVisualBlockOnLastCharOfLine() {
    doTest(
      listOf("<C-V>", "x"),
      "fo${c}o\n", "fo\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  fun testDeleteCharVisualBlockOnEmptyLinesDoesntDeleteAnything() {
    setupChecks {
      this.neoVim.ignoredRegisters = setOf('1', '"')
    }
    doTest(
      listOf("<C-V>", "j", "x"),
      "\n\n", "\n\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-781 |CTRL-V| |j|
  fun testDeleteCharVisualBlockWithEmptyLineInTheMiddle() {
    doTest(
      listOf("l", "<C-V>", "jj", "x"),
      """
        foo
        
        bar
        
      """.trimIndent(),
      """
        fo
        
        br
        
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-781 |CTRL-V| |j|
  @VimBehaviorDiffers(description = "Different registers content")
  fun testDeleteCharVisualBlockWithShorterLineInTheMiddle() {
    doTest(
      listOf("l", "<C-V>", "jj", "x"),
      """
        foo
        x
        bar
        
      """.trimIndent(),
      """
        fo
        x
        br
        
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-845 |CTRL-V| |x|
  fun testDeleteVisualBlockOneCharWide() {
    configureByText(
      """
  foo
  bar
  
      """.trimIndent()
    )
    typeText(injector.parser.parseKeys("<C-V>" + "j" + "x"))
    assertState(
      """
  oo
  ar
  
      """.trimIndent()
    )
  }

  // |r|
  fun testReplaceOneChar() {
    doTest("rx", "b${c}ar\n", "b${c}xr\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
  }

  // |r|
  @VimBehaviorDiffers(originalVimAfter = "foXX${c}Xr\n")
  fun testReplaceMultipleCharsWithCount() {
    doTest("3rX", "fo${c}obar\n", "fo${c}XXXr\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
  }

  // |r|
  fun testReplaceMultipleCharsWithCountPastEndOfLine() {
    doTest("6rX", "fo${c}obar\n", "fo${c}obar\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
  }

  // |r|
  @VimBehaviorDiffers(description = "Different caret position")
  fun testReplaceMultipleCharsWithVisual() {
    doTest(
      listOf("v", "ll", "j", "rZ"),
      """
        fo${c}obar
        foobaz
        
      """.trimIndent(),
      """
        foZZZZ
        ZZZZZz
        
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // |r|
  fun testReplaceOneCharWithNewline() {
    doTest(
      "r<Enter>",
      """    fo${c}obar
foobaz
""",
      """    fo
    bar
foobaz
""",
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // |r|
  @VimBehaviorDiffers(description = "Different caret position")
  fun testReplaceCharWithNewlineAndCountAddsOnlySingleNewline() {
    doTest(
      "3r<Enter>",
      """    fo${c}obar
foobaz
""",
      """    fo
    r
foobaz
""",
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // |s|
  fun testReplaceOneCharWithText() {
    doTest("sxy<Esc>", "b${c}ar\n", "bx${c}yr\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
  }

  // |s|
  fun testReplaceMultipleCharsWithTextWithCount() {
    doTest(
      "3sxy<Esc>",
      "fo${c}obar\n", "fox${c}yr\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // |s|
  fun testReplaceMultipleCharsWithTextWithCountPastEndOfLine() {
    doTest(
      "99sxyz<Esc>",
      """
        foo${c}bar
        biff
        
      """.trimIndent(),
      """
        fooxy${c}z
        biff
        
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // |R|
  fun testReplaceMode() {
    doTest("Rbaz<Esc>", "foo${c}bar\n", "fooba${c}z\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
  }

  // |R| |i_<Insert>|
  @VimBehaviorDiffers(description = "Different caret position")
  fun testReplaceModeSwitchToInsertModeAndBack() {
    doTest(
      "RXXX<Ins>YYY<Ins>ZZZ<Esc>",
      "aaa${c}bbbcccddd\n", "aaaXXXYYYZZ${c}Zddd\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // |i| |i_<Insert>|
  @TestWithoutNeovim(SkipNeovimReason.UNCLEAR, "<INS> works strange")
  fun testInsertModeSwitchToReplaceModeAndBack() {
    doTest(
      "iXXX<Ins>YYY<Ins>ZZZ<Esc>",
      "aaa${c}bbbcccddd\n", "aaaXXXYYYZZ${c}Zcccddd\n", VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  // VIM-511 |.|
  @TestWithoutNeovim(SkipNeovimReason.UNCLEAR, "Backspace workspace strange")
  fun testRepeatWithBackspaces() {
    doTest(
      listOf("ce", "foo", "<BS><BS><BS>", "foo", "<Esc>", "j0", "."),
      """
        ${c}foo baz
        baz quux
        
      """.trimIndent(),
      """
        foo baz
        fo${c}o quux
        
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-511 |.|
  @TestWithoutNeovim(SkipNeovimReason.DIFFERENT)
  fun testRepeatWithParensAndQuotesAutoInsertion() {
    configureByJavaText(
      """
  class C $c{
  }
  
      """.trimIndent()
    )
    typeText(injector.parser.parseKeys("o" + "foo(\"<Right>, \"<Right><Right>;" + "<Esc>" + "."))
    assertState(
      """class C {
    foo("", "");
    foo("", "");
}
"""
    )
  }

  // VIM-511 |.|
  @TestWithoutNeovim(SkipNeovimReason.DIFFERENT)
  fun testDeleteBothParensAndStartAgain() {
    configureByJavaText(
      """
  class C $c{
  }
  
      """.trimIndent()
    )
    typeText(injector.parser.parseKeys("o" + "C(" + "<BS>" + "(int i) {}" + "<Esc>" + "."))
    assertState(
      """class C {
    C(int i) {}
    C(int i) {}
}
"""
    )
  }

  // VIM-613 |.|
  fun testDeleteEndOfLineAndAgain() {
    configureByText(
      """
  $c- 1
  - 2
  - 3
  
      """.trimIndent()
    )
    typeText(injector.parser.parseKeys("d$" + "j" + "."))
    assertState(
      """
  
  
  - 3
  
      """.trimIndent()
    )
  }

  // VIM-511 |.|
  @TestWithoutNeovim(SkipNeovimReason.DIFFERENT)
  fun testAutoCompleteCurlyBraceWithEnterWithinFunctionBody() {
    configureByJavaText(
      """
  class C $c{
  }
  
      """.trimIndent()
    )
    typeText(injector.parser.parseKeys("o" + "C(" + "<BS>" + "(int i) {" + "<Enter>" + "i = 3;" + "<Esc>" + "<Down>" + "."))
    assertState(
      """class C {
    C(int i) {
        i = 3;
    }
    C(int i) {
        i = 3;
    }
}
"""
    )
  }

  // VIM-1067 |.|
  @TestWithoutNeovim(SkipNeovimReason.DIFFERENT)
  fun testRepeatWithInsertAfterLineEnd() {
    // Case 1
    configureByText(
      """
  $c- 1
  - 2
  - 3
  
      """.trimIndent()
    )
    typeText(injector.parser.parseKeys("A" + "<BS>" + "<Esc>" + "j" + "."))
    assertState(
      """
  - 
  - 
  - 3
  
      """.trimIndent()
    )

    // Case 2
    configureByText(
      """
  $c- 1
  - 2
  - 3
  
      """.trimIndent()
    )
    typeText(injector.parser.parseKeys("A" + "4" + "<BS>" + "<Esc>" + "j" + "."))
    assertState(
      """
  - 1
  - 2
  - 3
  
      """.trimIndent()
    )

    // Case 3
    configureByText(
      """
  $c- 1
  - 2
  - 3
  
      """.trimIndent()
    )
    typeText(injector.parser.parseKeys("A" + "<BS>" + "4" + "<Esc>" + "j" + "."))
    assertState(
      """
  - 4
  - 4
  - 3
  
      """.trimIndent()
    )
  }

  // VIM-287 |zc| |O|
  fun testInsertAfterFold() {
    configureByJavaText(
      """$c/**
 * I should be fold
 * a little more text
 * and final fold
 */
and some text after"""
    )
    typeText(injector.parser.parseKeys("zc" + "G" + "O"))
    assertState(
      """/**
 * I should be fold
 * a little more text
 * and final fold
 */
$c
and some text after"""
    )
  }

  // VIM-287 |zc| |o|
  @TestWithoutNeovim(SkipNeovimReason.FOLDING)
  fun testInsertBeforeFold() {
    configureByJavaText(
      """
          $c/**
           * I should be fold
           * a little more text
           * and final fold
           */
          and some text after
      """.trimIndent()
    )

    myFixture.editor.foldingModel.runBatchFoldingOperation {
      CodeFoldingManager.getInstance(myFixture.project).updateFoldRegions(myFixture.editor)
      FoldingUtil.findFoldRegionStartingAtLine(myFixture.editor, 0)!!.isExpanded = false
    }

    typeText(injector.parser.parseKeys("o"))
    assertState(
      """
            /**
             * I should be fold
             * a little more text
             * and final fold
             */
            $c
            and some text after
      """.trimIndent()
    )
  }

  fun testRepeatChangeWordDoesNotBreakNextRepeatFind() {
    doTest(
      "fXcfYPATATA<Esc>fX.;.", "${c}aaaaXBBBBYaaaaaaaXBBBBYaaaaaaXBBBBYaaaaaaaa\n",
      "aaaaPATATAaaaaaaaPATATAaaaaaaPATATAaaaaaaaa\n", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  fun testRepeatReplace() {
    configureByText("${c}foobarbaz spam\n")
    typeText(injector.parser.parseKeys("R"))
    assertMode(VimStateMachine.Mode.REPLACE)
    typeText(injector.parser.parseKeys("FOO" + "<Esc>" + "l" + "2."))
    assertState("FOOFOOFO${c}O spam\n")
    assertMode(VimStateMachine.Mode.COMMAND)
  }

  fun testDownMovementAfterDeletionToStart() {
    doTest(
      "ld^j",
      """
        lorem ${c}ipsum dolor sit amet
        lorem ipsum dolor sit amet
      """.trimIndent(),
      """
        psum dolor sit amet
        ${c}lorem ipsum dolor sit amet
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun testDownMovementAfterDeletionToPrevWord() {
    doTest(
      "ldbj",
      """
        lorem$c ipsum dolor sit amet
        lorem ipsum dolor sit amet
      """.trimIndent(),
      """
        ipsum dolor sit amet
        ${c}lorem ipsum dolor sit amet
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun testDownMovementAfterChangeToPrevWord() {
    doTest(
      "lcb<Esc>j",
      """
        lorem$c ipsum dolor sit amet
        lorem ipsum dolor sit amet
      """.trimIndent(),
      """
        ipsum dolor sit amet
        ${c}lorem ipsum dolor sit amet
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun testDownMovementAfterChangeToLineStart() {
    doTest(
      "lc^<Esc>j",
      """
        lorem$c ipsum dolor sit amet
        lorem ipsum dolor sit amet
      """.trimIndent(),
      """
        ipsum dolor sit amet
        ${c}lorem ipsum dolor sit amet
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun testUpMovementAfterDeletionToStart() {
    doTest(
      "ld^k",
      """
        lorem ipsum dolor sit amet
        lorem ${c}ipsum dolor sit amet
      """.trimIndent(),
      """
        ${c}lorem ipsum dolor sit amet
        psum dolor sit amet
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun testUpMovementAfterChangeToPrevWord() {
    doTest(
      "lcb<Esc>k",
      """
        lorem ipsum dolor sit amet
        lorem$c ipsum dolor sit amet
      """.trimIndent(),
      """
        ${c}lorem ipsum dolor sit amet
        ipsum dolor sit amet
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  // VIM-714 |v|
  fun testDeleteVisualColumnPositionOneLine() {
    doTest(
      "vwxj",
      """
        ${c}lorem ipsum dolor sit amet
        lorem ipsum dolor sit amet
        
      """.trimIndent(),
      """
        psum dolor sit amet
        ${c}lorem ipsum dolor sit amet
        
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  // VIM-714 |v|
  fun testDeleteVisualColumnPositionMultiLine() {
    doTest(
      "v3wfixj",
      """
        gaganis ${c}gaganis gaganis
        gaganis gaganis gaganis
        gaganis gaganis gaganis
        
      """.trimIndent(),
      """
        gaganis s gaganis
        gaganis ${c}gaganis gaganis
        
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }

  fun testChangeSameLine() {
    doTest(
      "d_",
      """
        line 1
        line$c 2
        line 3
      """.trimIndent(),
      """
        line 1
        ${c}line 3
      """.trimIndent(),
      VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE
    )
  }
}
