/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.ex.implementation.functions

import org.jetbrains.plugins.ideavim.VimTestCase

class BasicStringFunctions : VimTestCase() {

  fun `test toupper`() {
    configureByText("\n")
    typeText(commandToKeys("echo toupper('Vim is awesome')"))
    assertExOutput("VIM IS AWESOME\n")
  }

  fun `test tolower`() {
    configureByText("\n")
    typeText(commandToKeys("echo toupper('Vim is awesome')"))
    assertExOutput("vim is awesome\n")
  }

  fun `test join`() {
    configureByText("\n")
    typeText(commandToKeys("echo join(['Vim', 'is', 'awesome'], '_')"))
    assertExOutput("Vim_is_awesome\n")
  }

  fun `test join without second argument`() {
    configureByText("\n")
    typeText(commandToKeys("echo join(['Vim', 'is', 'awesome'])"))
    assertExOutput("Vim is awesome\n")
  }

  fun `test join with wrong first argument type`() {
    configureByText("\n")
    typeText(commandToKeys("echo join('Vim is awesome')"))
    assertPluginError(true)
    assertPluginErrorMessageContains("E714: List required")
  }
}
