#!/bin/bash

# Test Script for Network Programming Assignment
# Tests all three servers: TCP, UDP, and NIO

echo "=========================================="
echo "Network Programming - Server Test Script"
echo "=========================================="
echo ""

# Check if servers are running
check_port() {
    local port=$1
    local name=$2
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        echo "✓ $name is running on port $port"
        return 0
    else
        echo "✗ $name is NOT running on port $port"
        return 1
    fi
}

echo "Checking server status..."
echo ""

check_port 5000 "TCP Chat Server"
tcp_status=$?

check_port 5001 "NIO File Server"  
nio_status=$?

check_port 5002 "UDP Health Server"
udp_status=$?

echo ""
echo "=========================================="
echo ""

if [ $tcp_status -eq 0 ] && [ $nio_status -eq 0 ] && [ $udp_status -eq 0 ]; then
    echo "🚀 ALL SERVERS ARE RUNNING!"
    echo ""
    echo "Quick Test Commands:"
    echo ""
    echo "1. Test TCP Chat Server:"
    echo "   telnet localhost 5000"
    echo ""
    echo "2. Test NIO File Server:"
    echo "   telnet localhost 5001"
    echo "   (then type: LIST)"
    echo ""
    echo "3. Test UDP Health Server:"
    echo "   echo 'PING' | nc -u localhost 5002"
    echo "   OR"
    echo "   java -cp target/classes com.network.udp.UdpHealthClient"
    echo ""
else
    echo "⚠️  Some servers are not running!"
    echo ""
    echo "To start all servers:"
    echo "   java -cp target/classes com.network.MainServer"
    echo ""
fi

echo "=========================================="
