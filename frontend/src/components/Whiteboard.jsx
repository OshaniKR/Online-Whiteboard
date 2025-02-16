import React, { useRef, useEffect, useState } from "react";
import io from "socket.io-client";

// Connect to Java Server on port 5000
const socket = io("http://localhost:5000");

const Whiteboard = () => {
  const canvasRef = useRef(null);
  const ctxRef = useRef(null);
  const [drawing, setDrawing] = useState(false);
  const [color, setColor] = useState("#000000");
  const [lastPosition, setLastPosition] = useState(null); // Store last position

  useEffect(() => {
    const canvas = canvasRef.current;
    canvas.width = window.innerWidth * 0.8;
    canvas.height = window.innerHeight * 0.6;
    const ctx = canvas.getContext("2d");
    ctx.lineWidth = 3;
    ctx.lineCap = "round";
    ctxRef.current = ctx;

    // Handle incoming draw events from the server
    socket.on("draw", (drawMessage) => {
      const { x, y, color } = drawMessage;
      drawLine(x, y, x, y, color, false); // Drawing the point
    });

    // Handle clear event from the server
    socket.on("clear", () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
    });

    // Resize handler
    const handleResize = () => {
      const tempCanvas = document.createElement("canvas");
      tempCanvas.width = canvas.width;
      tempCanvas.height = canvas.height;
      tempCanvas.getContext("2d").drawImage(canvas, 0, 0);
      
      canvas.width = window.innerWidth * 0.8;
      canvas.height = window.innerHeight * 0.6;
      ctx.drawImage(tempCanvas, 0, 0);
    };

    window.addEventListener("resize", handleResize);

    return () => {
      socket.off("draw");
      socket.off("clear");
      window.removeEventListener("resize", handleResize);
    };
  }, []);

  const startDrawing = (e) => {
    setDrawing(true);
    const { offsetX, offsetY } = e.nativeEvent;
    setLastPosition({ x: offsetX, y: offsetY });
  };

  const stopDrawing = () => {
    setDrawing(false);
    setLastPosition(null);
  };

  const draw = (e) => {
    if (!drawing || !lastPosition) return;

    const { offsetX, offsetY } = e.nativeEvent;
    drawLine(lastPosition.x, lastPosition.y, offsetX, offsetY, color, true);
    setLastPosition({ x: offsetX, y: offsetY });
  };

  const drawLine = (x, y, x2, y2, color, emit) => {
    const ctx = ctxRef.current;
    ctx.strokeStyle = color;
    ctx.beginPath();
    ctx.moveTo(x, y);
    ctx.lineTo(x2, y2);
    ctx.stroke();
    ctx.closePath();

    if (emit) {
      // Send the DrawMessage to the server
      const drawMessage = { x, y, color };
      socket.emit("draw", drawMessage); // Emit to server to broadcast to all clients
    }
  };

  const clearCanvas = () => {
    ctxRef.current.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height);
    socket.emit("clear"); // Emit clear event to the server to clear other clients' canvases
  };

  return (
    <div>
      <h1>Online Whiteboard</h1>
      <canvas
        ref={canvasRef}
        onMouseDown={startDrawing}
        onMouseUp={stopDrawing}
        onMouseMove={draw}
        onTouchStart={startDrawing}
        onTouchEnd={stopDrawing}
        onTouchMove={draw}
        style={{ border: "1px solid black", cursor: "crosshair" }}
      />
      <br />
      <input type="color" onChange={(e) => setColor(e.target.value)} value={color} />
      <button onClick={clearCanvas}>Clear</button>
    </div>
  );
};

export default Whiteboard;
