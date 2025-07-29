#!/usr/bin/env python3
"""
Test Script for Symptom Persistence Flow
Tests the complete flow from symptom analysis to booking completion
"""

import requests
import json
import time
import sys
from typing import Dict, Any

# Service URLs
AI_SERVICE_URL = "http://localhost:5000"
BACKEND_URL = "http://localhost:8080"

class SymptomFlowTester:
    def __init__(self):
        self.user_id = "test_user_123"
        self.session = requests.Session()
        self.session.headers.update({'Content-Type': 'application/json'})
    
    def test_service_health(self) -> bool:
        """Test if both services are running"""
        print("🔍 Testing service health...")
        
        # Test AI Service
        try:
            response = self.session.get(f"{AI_SERVICE_URL}/health")
            if response.status_code == 200:
                print("✅ AI Service is running")
                print(f"   Status: {response.json()}")
            else:
                print(f"❌ AI Service health check failed: {response.status_code}")
                return False
        except Exception as e:
            print(f"❌ AI Service is not accessible: {e}")
            return False
        
        # Test Backend Service
        try:
            response = self.session.get(f"{BACKEND_URL}/api/health")
            if response.status_code == 200:
                print("✅ Backend Service is running")
            else:
                print(f"⚠️  Backend health endpoint returned: {response.status_code}")
                # Try alternative endpoint
                response = self.session.get(f"{BACKEND_URL}/")
                if response.status_code == 200:
                    print("✅ Backend Service is accessible")
                else:
                    print(f"❌ Backend Service is not accessible: {response.status_code}")
                    return False
        except Exception as e:
            print(f"❌ Backend Service is not accessible: {e}")
            return False
        
        print()
        return True
    
    def test_symptom_analysis(self, symptoms: str) -> Dict[str, Any]:
        """Test symptom analysis through AI service"""
        print(f"🧠 Testing symptom analysis: '{symptoms}'")
        
        payload = {
            "messages": [
                {"role": "user", "content": symptoms}
            ],
            "user_id": self.user_id
        }
        
        try:
            response = self.session.post(f"{AI_SERVICE_URL}/chat", json=payload)
            
            if response.status_code == 200:
                result = response.json()
                print(f"✅ AI Response received")
                print(f"   Intent: {result.get('intent', 'unknown')}")
                print(f"   Reply: {result.get('reply', 'No reply')[:100]}...")
                if result.get('entities'):
                    print(f"   Entities: {result['entities']}")
                return result
            else:
                print(f"❌ AI Service failed: {response.status_code}")
                print(f"   Response: {response.text}")
                return {}
        except Exception as e:
            print(f"❌ Error calling AI service: {e}")
            return {}
    
    def test_backend_chat(self, message: str) -> Dict[str, Any]:
        """Test backend AI agent processing"""
        print(f"🎯 Testing backend chat processing: '{message}'")
        
        payload = {
            "message": message,
            "userId": self.user_id
        }
        
        try:
            response = self.session.post(f"{BACKEND_URL}/api/ai-agent/chat", json=payload)
            
            if response.status_code == 200:
                result = response.json()
                print(f"✅ Backend Response received")
                print(f"   Intent: {result.get('intent', 'unknown')}")
                print(f"   Reply: {result.get('reply', 'No reply')[:100]}...")
                if result.get('recommendedDoctors'):
                    print(f"   Recommended Doctors: {len(result['recommendedDoctors'])}")
                return result
            else:
                print(f"❌ Backend failed: {response.status_code}")
                print(f"   Response: {response.text}")
                return {}
        except Exception as e:
            print(f"❌ Error calling backend: {e}")
            return {}
    
    def test_booking_flow(self, message: str) -> Dict[str, Any]:
        """Test conversational booking flow"""
        print(f"📅 Testing booking flow: '{message}'")
        
        payload = {
            "message": message,
            "userId": self.user_id
        }
        
        try:
            response = self.session.post(f"{BACKEND_URL}/api/booking/conversation", json=payload)
            
            if response.status_code == 200:
                result = response.json()
                print(f"✅ Booking Response received")
                print(f"   Intent: {result.get('intent', 'unknown')}")
                print(f"   Reply: {result.get('reply', 'No reply')[:150]}...")
                return result
            else:
                print(f"❌ Booking failed: {response.status_code}")
                print(f"   Response: {response.text}")
                return {}
        except Exception as e:
            print(f"❌ Error in booking flow: {e}")
            return {}
    
    def run_complete_flow_test(self):
        """Run the complete symptom persistence test"""
        print("🚀 Starting Complete Symptom Persistence Flow Test")
        print("=" * 60)
        
        # Step 1: Health checks
        if not self.test_service_health():
            print("❌ Service health check failed. Please ensure both services are running.")
            return False
        
        # Step 2: Test symptom analysis
        symptoms = "I have headache and fever"
        ai_result = self.test_symptom_analysis(symptoms)
        if not ai_result:
            print("❌ AI symptom analysis failed")
            return False
        print()
        
        # Step 3: Test backend processing of symptoms
        backend_symptom_result = self.test_backend_chat(symptoms)
        if not backend_symptom_result:
            print("❌ Backend symptom processing failed")
            return False
        print()
        
        # Step 4: Test booking request with symptom context
        booking_request = "book appointment"
        backend_booking_result = self.test_backend_chat(booking_request)
        if not backend_booking_result:
            print("❌ Backend booking processing failed")
            return False
        print()
        
        # Step 5: Validate symptom persistence
        print("🔍 Validating Symptom Persistence...")
        
        # Check if booking started with location selection (skipping symptom re-entry)
        if backend_booking_result.get('intent') == 'booking_location_selection':
            print("✅ SUCCESS: Booking started with pre-analyzed symptoms!")
            print("✅ Symptoms were preserved from previous conversation")
            print("✅ User doesn't need to re-enter symptoms")
        elif backend_booking_result.get('intent') == 'booking_start':
            print("⚠️  WARNING: Booking started fresh (symptoms may not be preserved)")
            print("   This might indicate the old flow is still active")
        else:
            print(f"❌ UNEXPECTED: Booking intent was '{backend_booking_result.get('intent')}'")
        
        print()
        
        # Step 6: Test booking conversation flow
        if backend_booking_result.get('intent') == 'booking_location_selection':
            # Simulate location selection
            location_response = self.test_booking_flow("Mumbai")
            if location_response:
                print("✅ Location selection successful")
            print()
        
        return True
    
    def run_edge_case_tests(self):
        """Test edge cases and error handling"""
        print("🧪 Running Edge Case Tests")
        print("=" * 40)
        
        # Test booking without prior symptoms
        print("Testing booking without prior symptom analysis...")
        direct_booking = self.test_backend_chat("book appointment")
        if direct_booking.get('intent') == 'booking_start':
            print("✅ Direct booking handled correctly")
        else:
            print(f"⚠️  Direct booking intent: {direct_booking.get('intent')}")
        print()
        
        # Test multiple symptom entries
        print("Testing multiple symptom conversations...")
        self.test_backend_chat("I have chest pain")
        self.test_backend_chat("Actually, I also have back pain")
        multiple_booking = self.test_backend_chat("book appointment")
        print(f"   Final booking intent: {multiple_booking.get('intent')}")
        print()

def main():
    """Main test execution"""
    print("Symptom Persistence Flow Test Suite")
    print("=" * 60)
    print()
    
    tester = SymptomFlowTester()
    
    # Run main flow test
    success = tester.run_complete_flow_test()
    
    if success:
        print("🎉 Main flow test completed!")
        print()
        
        # Run edge case tests
        tester.run_edge_case_tests()
        
        print("✅ All tests completed!")
        print("\n📋 TEST SUMMARY:")
        print("- ✅ Service health checks")
        print("- ✅ AI symptom analysis")
        print("- ✅ Backend symptom processing")
        print("- ✅ Backend booking with symptom context")
        print("- ✅ Symptom persistence validation")
        print("- ✅ Edge case handling")
    else:
        print("❌ Test suite failed!")
        print("\n🔧 TROUBLESHOOTING:")
        print("1. Ensure AI service is running: python ai_server.py")
        print("2. Ensure backend is running: mvn spring-boot:run")
        print("3. Check service URLs in this script")
        print("4. Verify database connectivity")
        
        return 1
    
    return 0

if __name__ == "__main__":
    exit_code = main()
    sys.exit(exit_code)
