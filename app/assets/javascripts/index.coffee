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
    console.log("Received: " + message)
    switch message.type
      when "insert"
        editor.getSession().getDocument().insert(message.range.start, message.text)
      when "delete"
        editor.getSession().getDocument().remove(message.range)
      else
        console.log(message)
    localInserting = false
  ws

registerEditorEvents = (ws) ->
  editor.getSession().on "change", (event) =>
    if !localInserting
      switch event.data.action
        when "removeText"
          version++
          json = JSON.stringify({version: version, type: "delete", range: event.data.range})
          ws.send(json)
          console.log("remove: " + json)
        when "insertText"
          version++
          json = JSON.stringify({version: version, type: "insert", text: event.data.text, range: event.data.range})
          ws.send(json)
          console.log("insert: " + json)

testMarkers = () ->
 editor.setValue("asdadad adadasdaa fasfasda fasdfasdaf\ndasdasdasda sdasdasd dghfhjjhj hfhghgh\nadasdasd fasfafaf sadadasd")
 editor.session.addMarker(editor.selection.getWordRange(0, 10), "error")
 editor.session.addMarker(editor.selection.getWordRange(1, 10), "warning")
 editor.session.addMarker(editor.selection.getWordRange(2, 10), "info")

editor = initEditor()
#testMarkers()
ws = initWebSocket()
registerEditorEvents(ws)