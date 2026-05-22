async function testRegister() {
    const email = "test" + Date.now() + "@example.com";
    try {
        console.log("Registering:", email);
        const response = await fetch('http://localhost:4000/api/user/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name: "Test User", email: email, password: "password12345" })
        });
        console.log("Status:", response.status);
        const text = await response.text();
        console.log("Body:", text);
    } catch (e) {
        console.error(e);
    }
}
testRegister();
