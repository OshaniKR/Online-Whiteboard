import React, { useRef, useEffect, useState } from "react";
import io from "socket.io-client";

const socket = io("http://localhost:12345"); // Connect to Java Server

const Whiteboard = () => {
  const canvasRef = useRef(null);
  const ctxRef = useRef(null);
  const [drawing, setDrawing] = useState(false);
  const [color, setColor] = useState("#000000");

  useEffect(() => {
    const canvas = canvasRef.current;
    canvas.width = 800;
    canvas.height = 600;
    const ctx = canvas.getContext("2d");
    ctx.lineWidth = 3;
    ctx.lineCap = "round";
    ctxRef.current = ctx;

    socket.on("draw", ({ x, y, x2, y2, color }) => {
      drawLine(x, y, x2, y2, color, false);
    });

    socket.on("clear", () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
    });

    return () => {
      socket.off("draw");
      socket.off("clear");
    };
  }, []);

  const startDrawing = (e) => {
    setDrawing(true);
  };

  const stopDrawing = () => {
    setDrawing(false);
  };

  const draw = (e) => {
    if (!drawing) return;

    const rect = canvasRef.current.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    drawLine(x, y, x, y, color, true);
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
      socket.emit("draw", { x, y, x2, y2, color });
    }
  };

  const clearCanvas = () => {
    ctxRef.current.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height);
    socket.emit("clear");
  };

  return (
    <div>
      <h1>Online Whiteboard</h1>
      <canvas
        ref={canvasRef}
        onMouseDown={startDrawing}
        onMouseUp={stopDrawing}
        onMouseMove={draw}
        style={{ border: "1px solid black", cursor: "crosshair" }}
      />
      <br />
      <input type="color" onChange={(e) => setColor(e.target.value)} />
      <button onClick={clearCanvas}>Clear</button>
    </div>
  );
};

export default Whiteboard;
