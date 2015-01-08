$ ->
  initEditor = () ->
    editor = ace.edit("editor")
    editor.setTheme("ace/theme/twilight")
    editor.getSession().setMode("ace/mode/scala")

    editor

  initWebSocket = () ->
    ws = new WebSocket $("body").data("ws-url")
    ws.onmessage = (event) ->
      message = JSON.parse event.data
      console.log("Received: " + message)
      switch message.type
        when "add"
          setCursorPosition($("#textarea").get(0), message.position)
          $("#textarea").val($("#textarea").val().substring(0, message.position) + String.fromCharCode(message.char) + $("#textarea").val().substring(message.position, $("#textarea").val().length))
        when "delete"
          setCursorPosition($("#textarea").get(0), message.position)
          $("#textarea").val($("#textarea").val().substring(0, message.position - 1) + $("#textarea").val().substring(message.position, $("#textarea").val().length))
        else
          console.log(message)

  registerEditorEvents = (ws) ->
    editor.getSession().on "change", (event) =>
             cursorPos = editor.getCursorPosition()
             console.log(cursorPos)
             switch event.keyCode
               when 8
                 console.log("Char deleted at position: " + (cursorPos + 1))
                 ws.send(JSON.stringify({version: 0, type: "delete", position: cursorPos + 1}))
               else
                 console.log("Char added: " + event.keyCode + " at position " + (cursorPos - 1))
                 ws.send(JSON.stringify({version: 0, type: "add", char: event.keyCode, position: cursorPos - 1}))

  editor = initEditor()
  ws = initWebSocket()
  registerEditorEvents(ws)

#  $("#textarea").on "keyup", (event) =>
#    cursorPos = getCursorPosition($("#textarea").get(0))
#    console.log("Cursorposition: " + cursorPos)
#    switch event.keyCode
#      when 8
#        console.log("Char deleted at position: " + (cursorPos + 1))
#        ws.send(JSON.stringify({version: 0, type: "delete", position: cursorPos + 1}))
#      else
#        console.log("Char added: " + event.keyCode + " at position " + (cursorPos - 1))
#        ws.send(JSON.stringify({version: 0, type: "add", char: event.keyCode, position: cursorPos - 1}))

#  getCursorPosition = (el) ->
#    pos = 0
#    if "selectionStart" of el
#      pos = el.selectionStart
#    else if "selection" of document
#      el.focus()
#      Sel = document.selection.createRange()
#      SelLength = document.selection.createRange().text.length
#      Sel.moveStart "character", -el.value.length
#      pos = Sel.text.length - SelLength
#    return pos
#
#  setCursorPosition = (el, pos) ->
#    if el.setSelectionRange
#      el.focus()
#      el.setSelectionRange(pos,pos)
#    else if el.createTextRange
#      range = el.createTextRange()
#      range.collapse(true)
#      range.moveEnd('character', pos)
#      range.moveStart('character', pos)
#      range.select()

