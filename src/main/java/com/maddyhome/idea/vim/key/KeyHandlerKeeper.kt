/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.key

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.actionSystem.TypedActionHandler

@Service
class KeyHandlerKeeper {

  lateinit var originalHandler: TypedActionHandler

  companion object {
    @JvmStatic
    fun getInstance(): KeyHandlerKeeper = service()
  }
}
