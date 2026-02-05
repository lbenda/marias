import React from "react";
import type { ConnectionState } from "../api/transport";

interface ConnectionIndicatorProps {
    status: ConnectionState;
    className?: string;
}

const STATUS_CONFIG: Record<ConnectionState, { color: string; label: string; icon: string }> = {
    disconnected: { color: "#dc3545", label: "Disconnected", icon: "●" },
    connecting: { color: "#ffc107", label: "Connecting...", icon: "◐" },
    "connected-websocket": { color: "#28a745", label: "Live", icon: "●" },
    "connected-longpoll": { color: "#28a745", label: "Connected", icon: "●" },
    "connected-shortpoll": { color: "#fd7e14", label: "Polling", icon: "◐" },
    reconnecting: { color: "#ffc107", label: "Reconnecting...", icon: "◐" },
};

export function ConnectionIndicator({ status, className }: ConnectionIndicatorProps) {
    const config = STATUS_CONFIG[status];

    return (
        <div
            className={className}
            style={{
                display: "inline-flex",
                alignItems: "center",
                gap: 6,
                padding: "4px 8px",
                borderRadius: 4,
                fontSize: 12,
                background: `${config.color}20`,
                border: `1px solid ${config.color}`,
            }}
            title={`Connection status: ${status}`}
        >
            <span
                style={{
                    color: config.color,
                    animation: status.includes("connecting") || status === "reconnecting"
                        ? "pulse 1s infinite"
                        : undefined,
                }}
            >
                {config.icon}
            </span>
            <span style={{ color: config.color, fontWeight: 500 }}>
                {config.label}
            </span>
            <style>{`
                @keyframes pulse {
                    0%, 100% { opacity: 1; }
                    50% { opacity: 0.5; }
                }
            `}</style>
        </div>
    );
}
