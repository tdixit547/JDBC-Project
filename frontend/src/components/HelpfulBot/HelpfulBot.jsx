import React, { useState, useRef, useEffect } from 'react';
import { Canvas, useFrame } from '@react-three/fiber';
import { Sphere, Cylinder } from '@react-three/drei';
import './HelpfulBot.css';

const RobotModel = () => {
  const group = useRef();

  
  useFrame((state) => {
    const t = state.clock.getElapsedTime();
    group.current.position.y = Math.sin(t * 2) * 0.1 - 0.5; // Bobbing

  });

  return (
    <group ref={group} scale={[1.3, 1.3, 1.3]}>
      {/* --- HEAD GROUP --- */}
      <group position={[0, 0.8, 0]}>
        {/* Head Shape */}
        <Sphere args={[0.65, 32, 32]} scale={[1, 0.85, 0.9]}>
            <meshStandardMaterial color="white" roughness={0.3} metalness={0.1} />
        </Sphere>

        {/* Ears */}
        <Sphere args={[0.18, 32, 32]} position={[-0.65, 0.1, 0]} scale={[1, 1, 0.5]}>
             <meshStandardMaterial color="white" />
        </Sphere>
        <Sphere args={[0.18, 32, 32]} position={[0.65, 0.1, 0]} scale={[1, 1, 0.5]}>
             <meshStandardMaterial color="white" />
        </Sphere>

        {/* Eyes */}
        <Sphere args={[0.09, 32, 32]} position={[-0.22, 0.05, 0.52]}>
          <meshStandardMaterial color="#FF4500" emissive="#FF4500" emissiveIntensity={0.6} />
        </Sphere>
        <Sphere args={[0.09, 32, 32]} position={[0.22, 0.05, 0.52]}>
          <meshStandardMaterial color="#FF4500" emissive="#FF4500" emissiveIntensity={0.6} />
        </Sphere>

        {/* Mouth (Smiling) - Half Torus Hack */}
        <mesh position={[0, -0.15, 0.52]} rotation={[0, 0, Math.PI]}>
            <torusGeometry args={[0.12, 0.03, 16, 32, Math.PI]} />
            <meshStandardMaterial color="#333" />
        </mesh>

        {/* Antenna - Bent Shape */}
        {/* Base Stem */}
        <Cylinder args={[0.025, 0.025, 0.3]} position={[0, 0.65, 0]}>
            <meshStandardMaterial color="white" />
        </Cylinder>
        {/* The Bend (Torus Segment) */}
        <mesh position={[0.15, 0.8, 0]} rotation={[0, 0, -Math.PI/2]}>
             <torusGeometry args={[0.15, 0.025, 16, 32, Math.PI/1.5]} />
             <meshStandardMaterial color="white" />
        </mesh>
        {/* The Ball Tip */}
        <Sphere args={[0.12, 32, 32]} position={[0.35, 0.9, 0]}>
             <meshStandardMaterial color="white" />
        </Sphere>
      </group>

      {/* --- BODY --- */}
      <Sphere args={[0.45, 32, 32]} position={[0, -0.2, 0]} scale={[1, 1.3, 0.9]}>
         <meshStandardMaterial color="white" />
      </Sphere>


      {/* Arms Removed */}


      {/* Legs (Feet) */}
      <Sphere args={[0.18, 32, 32]} position={[-0.25, -0.85, 0.1]} scale={[1, 0.7, 1.5]}>
         <meshStandardMaterial color="white" />
      </Sphere>
      <Sphere args={[0.18, 32, 32]} position={[0.25, -0.85, 0.1]} scale={[1, 0.7, 1.5]}>
         <meshStandardMaterial color="white" />
      </Sphere>

    </group>
  );
};

const HelpfulBot = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    { text: "Hi! I'm your food buddy. Hungry?", sender: 'bot' }
  ]);
  const [inputText, setInputText] = useState("");
  const messagesEndRef = useRef(null);

  const toggleChat = () => {
    setIsOpen(!isOpen);
    // Add prompt if opening first time
    if (!isOpen && messages.length === 1) {
        // Maybe logic here later
    }
  };

  const handleSend = () => {
    if (!inputText.trim()) return;
    
    const newMessages = [...messages, { text: inputText, sender: 'user' }];
    setMessages(newMessages);
    setInputText("");

    // Simple auto-reply simulation
    setTimeout(() => {
        let reply = "That sounds delicious! Check out our menu.";
        const lowerInput = inputText.toLowerCase();
        if (lowerInput.includes('salad')) reply = "Our Greek Salad is a top choice!";
        else if (lowerInput.includes('cake')) reply = "The Butterscotch Cake is divine.";
        else if (lowerInput.includes('help')) reply = "I can help you browse food or track orders.";
        else if (lowerInput.includes('hello') || lowerInput.includes('hi')) reply = "Hello friend! Ready to order?";
        
        setMessages(prev => [...prev, { text: reply, sender: 'bot' }]);
    }, 800);
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') handleSend();
  };

  // Auto scroll to bottom
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  return (
    <div className="helpful-bot-container">
      {isOpen && (
        <div className="bot-chat-window">
          <div className="bot-chat-header">
            <span>Food Bot 🤖</span>
            <button onClick={() => setIsOpen(false)}>×</button>
          </div>
          <div className="bot-chat-messages">
            {messages.map((msg, index) => (
              <div key={index} className={`message ${msg.sender}`}>
                {msg.text}
              </div>
            ))}
            <div ref={messagesEndRef} />
          </div>
          <div className="bot-chat-input">
            <input 
                type="text" 
                placeholder="Ask me anything..." 
                value={inputText}
                onChange={(e) => setInputText(e.target.value)}
                onKeyPress={handleKeyPress}
            />
            <button onClick={handleSend}>➤</button>
          </div>
        </div>
      )}
      
      <div className="bot-canvas-wrapper" onClick={toggleChat} title="Need help?">
        <Canvas camera={{ position: [0, 0, 4.5], fov: 50 }}>
          <directionalLight position={[5, 10, 5]} intensity={1} />
          <ambientLight intensity={0.6} />
          <RobotModel />
        </Canvas>
        <div className="bot-glow"></div>
      </div>
    </div>
  );
};

export default HelpfulBot;
