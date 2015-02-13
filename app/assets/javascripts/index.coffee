version = 0
localInserting = false

initEditor = () ->
  Range = ace.require("ace/range").Range
  editor = ace.edit("editor")
  editor.setTheme("ace/theme/twilight")
  editor.setFontSize(16)
  editor.getSession().setMode("ace/mode/scala")
  editor

initWebSocket = () ->
  ws = new WebSocket $("body").data("ws-url")
  ws.onmessage = (event) ->
    localInserting = true
    message = JSON.parse event.data
    switch message.type
      when "insert"
        console.log("insert: ")
        console.log(message)
        editor.getSession().getDocument().insert(message.range.start, message.text)
      when "insertLines"
        console.log("insertLines:")
        console.log(message)
        editor.getSession().getDocument().insertLines(message.range.start.row, message.lines)
      when "delete"
        console.log("delete: ")
        console.log(message)
        editor.getSession().getDocument().remove(message.range)
      when "deleteLines"
        console.log("deleteLines: ")
        console.log(message)
        editor.getSession().getDocument().removeLines(message.range.start, message.range.end)
    localInserting = false
  ws

registerEditorEvents = (ws) ->
  editor.getSession().on "change", (event) =>
    if !localInserting
      console.log(event.data)
      switch event.data.action
        when "removeText"
          version++
          json = JSON.stringify({version: version, type: "delete", range: event.data.range})
          ws.send(json)
          console.log("remove: " + json)
        when "removeLines"
          version++
          json = JSON.stringify({version: version, type: "delete", range: event.data.range})
          ws.send(json)
          console.log("removeLines: " + json)
        when "insertText"
          version++
          json = JSON.stringify({version: version, type: "insert", text: event.data.text, range: event.data.range})
          ws.send(json)
          console.log("insert: " + json)
        when "insertLines"
          version++
          json = JSON.stringify({version: version, type: "insertLines", lines: event.data.lines, range: event.data.range})
          ws.send(json)
          console.log("insertLines: " + json)

testMarkers = () ->
 editor.setValue("asdadad adadasdaa fasfasda fasdfasdaf\ndasdasdasda sdasdasd dghfhjjhj hfhghgh\nadasdasd fasfafaf sadadasd")
 editor.session.addMarker(editor.selection.getWordRange(0, 10), "error")
 editor.session.addMarker(editor.selection.getWordRange(1, 10), "warning")
 editor.session.addMarker(editor.selection.getWordRange(2, 10), "info")

editor = initEditor()
#testMarkers()
ws = initWebSocket()
registerEditorEvents(ws)