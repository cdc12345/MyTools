(${input$list}.stream().map(${field$decorator}).collect(Collectors.joining(${input$delimiter},${input$prefix},${input$suffix})))
