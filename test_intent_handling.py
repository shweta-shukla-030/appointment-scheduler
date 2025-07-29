#!/usr/bin/env python3
"""
Quick Intent Handling Test
Tests the routing and intent handling after standardization
"""

import requests
import json
import time

# Service URLs
BACKEND_URL = "http://localhost:8080"

def test_intent_handling():
    """Test intent routing after standardization"""
    print("🎯 Testing Intent Handling After Standardization")
    print("=" * 50)
    
    # Use timestamp to ensure unique user ID each run
    user_id = f"test_user_intent_{int(time.time())}"
    print(f"Using user ID: {user_id}")
    
    # Step 0: Clear any existing conversation state
    print("\n0️⃣ Clearing any existing conversation state...")
    try:
        clear_payload = {"userId": user_id}
        clear_response = requests.post(f"{BACKEND_URL}/api/booking/clear-state", json=clear_payload)
        print(f"   State cleared (status: {clear_response.status_code})")
    except:
        print("   No clear-state endpoint, continuing...")
    
    # Test 1: AI Agent with Symptom Check
    print("\n1️⃣ Testing AI Agent with symptom input...")
    payload = {
        "message": "I have a headache and fever",
        "userId": user_id
    }
    
    try:
        response = requests.post(f"{BACKEND_URL}/api/ai/chat", json=payload)
        if response.status_code == 200:
            result = response.json()
            print(f"✅ Success! Intent: {result.get('intent')}")
            print(f"   Reply: {result.get('reply', 'No reply')[:100]}...")
            
            # Check if we get symptom_check intent
            if result.get('intent') == 'symptom_check':
                print("   ✅ Correct intent returned: symptom_check")
                
                # Add small delay to ensure context is stored
                time.sleep(0.5)
                
                # Test 2: Follow up with "book appointment" to trigger booking
                print("\n2️⃣ Testing booking initiation with 'book appointment'...")
                payload2 = {
                    "message": "book appointment",
                    "userId": user_id
                }
                
                response2 = requests.post(f"{BACKEND_URL}/api/ai/chat", json=payload2)
                if response2.status_code == 200:
                    result2 = response2.json()
                    print(f"✅ Success! Intent: {result2.get('intent')}")
                    print(f"   Reply: {result2.get('reply', 'No reply')[:100]}...")
                    
                    # Check if we get booking_location_selection intent (should start booking)
                    if result2.get('intent') == 'booking_location_selection':
                        print("   ✅ Correct intent returned: booking_location_selection")
                        print("   🎉 Intent routing is working correctly!")
                        
                        # Test 3: Test location selection in booking conversation
                        print("\n3️⃣ Testing location selection in booking conversation...")
                        payload3 = {
                            "message": "1",  # Select first location
                            "userId": user_id
                        }
                        
                        response3 = requests.post(f"{BACKEND_URL}/api/booking/conversation", json=payload3)
                        if response3.status_code == 200:
                            result3 = response3.json()
                            print(f"✅ Success! Intent: {result3.get('intent')}")
                            print(f"   Reply: {result3.get('reply', 'No reply')[:100]}...")
                            
                            if result3.get('intent') == 'booking_date_selection':
                                print("   ✅ Correct intent returned: booking_date_selection")
                                print("   🎉 Booking conversation flow is working!")
                                
                                # Test 4: Test date selection
                                print("\n4️⃣ Testing date selection...")
                                payload4 = {
                                    "message": "2025-07-23",  # Select tomorrow's date
                                    "userId": user_id
                                }
                                
                                response4 = requests.post(f"{BACKEND_URL}/api/booking/conversation", json=payload4)
                                if response4.status_code == 200:
                                    result4 = response4.json()
                                    print(f"✅ Success! Intent: {result4.get('intent')}")
                                    print(f"   Reply: {result4.get('reply', 'No reply')[:100]}...")
                                    
                                    if result4.get('intent') == 'booking_time_selection':
                                        print("   ✅ Correct intent returned: booking_time_selection")
                                        
                                        # Test 5: Test time slot selection
                                        print("\n5️⃣ Testing time slot selection...")
                                        payload5 = {
                                            "message": "1",  # Select first time slot
                                            "userId": user_id
                                        }
                                        
                                        response5 = requests.post(f"{BACKEND_URL}/api/booking/conversation", json=payload5)
                                        if response5.status_code == 200:
                                            result5 = response5.json()
                                            print(f"✅ Success! Intent: {result5.get('intent')}")
                                            print(f"   Reply: {result5.get('reply', 'No reply')[:100]}...")
                                            
                                            if result5.get('intent') == 'booking_reason_input':
                                                print("   ✅ Correct intent returned: booking_reason_input")
                                                
                                                # Test 6: Test reason input and final booking
                                                print("\n6️⃣ Testing reason input and final booking...")
                                                payload6 = {
                                                    "message": "Follow-up for headache treatment",
                                                    "userId": user_id
                                                }
                                                
                                                response6 = requests.post(f"{BACKEND_URL}/api/booking/conversation", json=payload6)
                                                if response6.status_code == 200:
                                                    result6 = response6.json()
                                                    print(f"✅ Success! Intent: {result6.get('intent')}")
                                                    print(f"   Reply: {result6.get('reply', 'No reply')[:150]}...")
                                                    
                                                    if result6.get('intent') == 'booking_success':
                                                        print("   ✅ Correct intent returned: booking_success")
                                                        print("   🎉 COMPLETE BOOKING FLOW SUCCESSFUL!")
                                                    else:
                                                        print(f"   ⚠️  Expected 'booking_success', got '{result6.get('intent')}'")
                                                else:
                                                    print(f"   ❌ Reason input failed: {response6.status_code}")
                                            else:
                                                print(f"   ⚠️  Expected 'booking_reason_input', got '{result5.get('intent')}'")
                                        else:
                                            print(f"   ❌ Time slot selection failed: {response5.status_code}")
                                    else:
                                        print(f"   ⚠️  Expected 'booking_time_selection', got '{result4.get('intent')}'")
                                else:
                                    print(f"   ❌ Date selection failed: {response4.status_code}")
                            else:
                                print(f"   ⚠️  Expected 'booking_date_selection', got '{result3.get('intent')}'")
                        else:
                            print(f"   ❌ Booking conversation failed: {response3.status_code}")
                    else:
                        print(f"   ⚠️  Expected 'booking_location_selection', got '{result2.get('intent')}'")
                        print("   📝 Note: This might be 'book_appointment' intent which is also correct")
                        
                        # If it's book_appointment intent, that's also valid - it means booking initiated
                        if result2.get('intent') == 'book_appointment':
                            print("   ✅ Alternative correct intent: book_appointment")
            else:
                print(f"   ⚠️  Expected 'symptom_check', got '{result.get('intent')}'")
        else:
            print(f"❌ Failed: {response.status_code}")
            print(f"   Response: {response.text}")
    except Exception as e:
        print(f"❌ Error: {e}")

    print("\n" + "="*50)
    print("🔧 CORRECT FLOW SUMMARY:")
    print("1. User describes symptoms → symptom_check intent")
    print("2. User says 'book appointment' → booking_location_selection intent")
    print("3. User selects location → booking_date_selection intent")
    print("4. User selects date → booking_time_selection intent")
    print("5. User selects time slot → booking_reason_input intent")
    print("6. User provides reason → booking_success intent")
    print("7. Complete booking flow with appointment confirmation")
    print("="*50)

if __name__ == "__main__":
    test_intent_handling()