import React from "react";
import { Link, Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage";
import JoinPage from "./pages/JoinPage";
import GamePage from "./pages/GamePage";

export default function App() {
    return (
        <div style={{ maxWidth: 900, margin: "0 auto", padding: 16, fontFamily: "system-ui, sans-serif" }}>
            <header style={{ display: "flex", gap: 12, alignItems: "center", marginBottom: 16 }}>
                <h1 style={{ margin: 0, fontSize: 20 }}>Marias</h1>
                <nav style={{ display: "flex", gap: 12 }}>
                    <Link to="/">Create</Link>
                    <Link to="/join">Join</Link>
                </nav>
            </header>

            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/join" element={<JoinPage />} />
                <Route path="/game/:gameId" element={<GamePage />} />
            </Routes>
        </div>
    );
}
